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
package org.mdpnp.devices;

import java.lang.reflect.Method;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.topic.TopicDescription;
import com.rti.dds.topic.TypeSupport;

/**
 * @author Jeff Plourde
 *
 */
public class TopicUtil {
    public static TopicDescription lookupOrCreateTopic(DomainParticipant participant, String topicName, Class<? extends TypeSupport> clazz) {
        TopicDescription topic = participant.lookup_topicdescription(topicName);
        if (null == topic) {
            try {
                Method get_type_name = clazz.getMethod("get_type_name");
                String typeName = (String) get_type_name.invoke(null);
                clazz.getMethod("register_type", DomainParticipant.class, String.class).invoke(null, participant, typeName);
                topic = participant.create_topic(topicName, typeName, DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        return topic;
    }
}
