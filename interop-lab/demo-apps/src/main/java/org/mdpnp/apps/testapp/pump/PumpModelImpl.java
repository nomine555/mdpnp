/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.apps.testapp.pump;

import ice.InfusionObjectiveDataWriter;
import ice.InfusionStatusDataReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.QosProfiles;
import org.mdpnp.devices.TopicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;
import com.rti.dds.topic.TopicDescription;

/**
 * @author Jeff Plourde
 *
 */
public class PumpModelImpl implements PumpModel {

    private final List<Pump> pumps = Collections.synchronizedList(new ArrayList<Pump>());
    private PumpModelListener[] listeners = new PumpModelListener[0];

    private ice.InfusionStatusDataReader statusReader;
    private ReadCondition statusCondition;
    private ice.InfusionObjectiveDataWriter objectiveWriter;

    private Subscriber subscriber;
    private Publisher publisher;
    private EventLoop eventLoop;

    private final EventLoop.ConditionHandler infusionStatusHandler = new EventLoop.ConditionHandler() {
        private final ice.InfusionStatusSeq inf_seq = new ice.InfusionStatusSeq();
        private final SampleInfoSeq info_seq = new SampleInfoSeq();

        @Override
        public void conditionChanged(Condition condition) {
            try {
                for (;;) {
                    try {
                        statusReader.read_w_condition(inf_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (ReadCondition) condition);
                        for (int i = 0; i < info_seq.size(); i++) {
                            SampleInfo sampleInfo = (SampleInfo) info_seq.get(i);
                            if (0 != (sampleInfo.instance_state & InstanceStateKind.NOT_ALIVE_INSTANCE_STATE)) {
                                ice.InfusionStatus keyHolder = new ice.InfusionStatus();
                                statusReader.get_key_value(keyHolder, sampleInfo.instance_handle);
                                removePump(keyHolder.unique_device_identifier);
                            } else {
                                if (sampleInfo.valid_data) {
                                    ice.InfusionStatus s = (ice.InfusionStatus) inf_seq.get(i);
                                    updatePump(s, sampleInfo);
                                }
                            }
                        }
                    } finally {
                        statusReader.return_loan(inf_seq, info_seq);
                    }
                }
            } catch (RETCODE_NO_DATA noData) {

            } finally {

            }
        }
    };
    private static final Logger log = LoggerFactory.getLogger(PumpModelImpl.class);

    protected void removePump(String udi) {
        List<Pump> removed = new ArrayList<Pump>();
        synchronized (pumps) {
            ListIterator<Pump> litr = pumps.listIterator();
            while (litr.hasNext()) {
                Pump pump = litr.next();
                if (pump.getInfusionStatus().unique_device_identifier.equals(udi)) {
                    removed.add(pump);
                    litr.remove();
                }
            }
        }
        if (removed.isEmpty()) {
            log.warn("Couldn't find pump with udi to remove : " + udi);
        }
        for (Pump p : removed) {
            firePumpRemoved(p);
        }
    }

    protected void updatePump(ice.InfusionStatus status, SampleInfo sampleInfo) {
        Pump pump = null;
        synchronized (pumps) {
            ListIterator<Pump> itr = pumps.listIterator();
            while (itr.hasNext()) {
                pump = itr.next();
                if (status.unique_device_identifier.equals(pump.getInfusionStatus().unique_device_identifier)) {
                    break;
                } else {
                    pump = null;
                }
            }
        }
        if (pump != null) {
            pump.getInfusionStatus().copy_from(status);
            pump.getSampleInfo().copy_from(sampleInfo);
            firePumpChanged(pump);
        } else {
            pump = new PumpImpl(this, status, sampleInfo);
            pumps.add(pump);
            firePumpAdded(pump);
        }

    }

    @Override
    public int getCount() {
        return pumps.size();
    }

    @Override
    public Pump getPump(int i) {
        return pumps.get(i);
    }

    @Override
    public void addListener(PumpModelListener listener) {
        PumpModelListener[] oldListeners = this.listeners;
        PumpModelListener[] newListeners = new PumpModelListener[oldListeners.length + 1];
        System.arraycopy(oldListeners, 0, newListeners, 0, oldListeners.length);
        newListeners[newListeners.length - 1] = listener;
        this.listeners = newListeners;
    }

    @Override
    public boolean removeListener(PumpModelListener listener) {
        PumpModelListener[] oldListeners = this.listeners;
        List<PumpModelListener> newListeners = new ArrayList<PumpModelListener>();
        boolean found = false;
        for (PumpModelListener vml : oldListeners) {
            if (listener.equals(vml)) {
                found = true;
            } else {
                newListeners.add(vml);
            }
        }

        this.listeners = newListeners.toArray(new PumpModelListener[0]);
        return found;
    }

    protected void firePumpAdded(Pump p) {
        PumpModelListener[] listeners = this.listeners;
        for (PumpModelListener pml : listeners) {
            pml.pumpAdded(this, p);
        }
    }

    protected void firePumpRemoved(Pump p) {
        PumpModelListener[] listeners = this.listeners;
        for (PumpModelListener pml : listeners) {
            pml.pumpRemoved(this, p);
        }
    }

    protected void firePumpChanged(Pump p) {
        PumpModelListener[] listeners = this.listeners;
        for (PumpModelListener pml : listeners) {
            pml.pumpChanged(this, p);
        }
    }

    public void start(Subscriber subscriber, Publisher publisher, EventLoop eventLoop) {
        this.subscriber = subscriber;
        this.publisher = publisher;
        this.eventLoop = eventLoop;

        // ice.InfusionStatusTypeSupport.register_type(subscriber.get_participant(),
        // ice.InfusionStatusTypeSupport.get_type_name());
        TopicDescription infusionStatusTopic = TopicUtil.lookupOrCreateTopic(subscriber.get_participant(), ice.InfusionStatusTopic.VALUE,
                ice.InfusionStatusTypeSupport.class);
        statusReader = (InfusionStatusDataReader) subscriber.create_datareader_with_profile(infusionStatusTopic, QosProfiles.ice_library,
                QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
        statusCondition = statusReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
                InstanceStateKind.ANY_INSTANCE_STATE);

        ice.InfusionObjectiveTypeSupport.register_type(subscriber.get_participant(), ice.InfusionObjectiveTypeSupport.get_type_name());
        Topic infusionObjectiveTopic = publisher.get_participant().create_topic(ice.InfusionObjectiveTopic.VALUE,
                ice.InfusionObjectiveTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        objectiveWriter = (InfusionObjectiveDataWriter) publisher.create_datawriter_with_profile(infusionObjectiveTopic, QosProfiles.ice_library,
                QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        eventLoop.addHandler(statusCondition, infusionStatusHandler);
    }

    public void stop() {
        if(publisher!=null) {
            publisher.delete_datawriter(objectiveWriter);
            objectiveWriter = null;
        }

        if(eventLoop != null) {
            eventLoop.removeHandler(statusCondition);
            statusReader.delete_readcondition(statusCondition);
            statusCondition = null;
            subscriber.delete_datareader(statusReader);
            statusReader = null;
        }

    }

    @Override
    public void setStop(Pump pump, boolean stop) {
        ice.InfusionObjective obj = new ice.InfusionObjective();
        obj.requestor = "ME";
        obj.unique_device_identifier = pump.getInfusionStatus().unique_device_identifier;
        obj.stopInfusion = stop;
        objectiveWriter.write(obj, InstanceHandle_t.HANDLE_NIL);
    }
}
