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
package org.mdpnp.apps.testapp;

import ice.DeviceConnectivity;
import ice.DeviceIdentity;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

@SuppressWarnings({ "serial", "rawtypes" })
/**
 * @author Jeff Plourde
 *
 */
public class DeviceListCellRenderer extends JComponent implements ListCellRenderer {

    private final JLabel icon = new JLabel();
    private final JLabel modelName = new JLabel(" ");
    private final JLabel connectionStatus = new JLabel(" ");
    private final JLabel udi = new JLabel(" ");
    private final JLabel hostname = new JLabel(" ");
    private final JLabel buildDescriptor = new JLabel(" ");

    private Dimension myDimension = null;

    @Override
    protected void paintComponent(Graphics g) {
        if (isOpaque()) {
            g.setColor(getBackground());
            myDimension = getSize(myDimension);
            g.fillRect(0, 0, myDimension.width, myDimension.height);
        }
        super.paintComponent(g);
    }

    private static final void addFinePrint(Font fineprint, String label, JComponent component, GridBagConstraints gbc, Container container) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnl.setBorder(new EmptyBorder(0, 0, 0, 0));
        pnl.setOpaque(false);
        JLabel lbl = new JLabel(label);
        pnl.add(lbl);
        lbl.setFont(fineprint);
        pnl.add(component);
        component.setFont(fineprint);
        container.add(pnl, gbc);
        gbc.gridy++;
    }
    
    public DeviceListCellRenderer() {
        super();
        setLayout(new BorderLayout());
        setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        setOpaque(true);

        add(icon, BorderLayout.WEST);

        JPanel text = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
                0, 0, 0), 0, 0);
        text.setOpaque(false);

        text.add(modelName, gbc);
        gbc.gridy++;
        text.add(connectionStatus, gbc);
        gbc.gridy++;
        
        Font fineprint = Font.decode("fixed-8");
        addFinePrint(fineprint, "Unique Device Id:", udi, gbc, text);
        addFinePrint(fineprint, "Hostname:", hostname, gbc, text);
        addFinePrint(fineprint, "Build:", buildDescriptor, gbc, text);

        add(text, BorderLayout.CENTER);

    }

    private final Border selectedBorder = new LineBorder(Color.blue, 1);

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Device device = value == null ? null : (Device) value;

        modelName.setFont(list.getFont());
        connectionStatus.setFont(list.getFont());

        if (null != device) {

            String udi = device.getUDI();
            if (null == udi) {
                this.udi.setText("<unknown UDI>");
            } else {
                this.udi.setText(udi);
            }

            hostname.setText(device.getHostname());
            

            DeviceIcon icon = device.getIcon();

            DeviceConnectivity dc = device.getDeviceConnectivity();
            if (null != dc) {
                if (icon != null) {
                    icon.setConnected(ice.ConnectionState.Connected.equals(dc.state));
                }
                connectionStatus.setText(dc.state.toString());
            } else {
                if (icon != null) {
                    icon.setConnected(true);
                }
                connectionStatus.setText("");
            }

            DeviceIdentity di = device.getDeviceIdentity();
            if (null != di) {
                modelName.setText(device.getMakeAndModel());
                buildDescriptor.setText(di.build);
            } else {
                modelName.setText(device.getParticipantData().participant_name.name);
                buildDescriptor.setText("DeviceIdentity not yet found.");
            }

            if (icon != null) {
                this.icon.setIcon(icon);
            } else {
                this.icon.setIcon(DeviceIcon.WHITE_SQUARE_ICON);
            }
        } else {
            udi.setText("<awaiting UDI>");
            connectionStatus.setText("");
            hostname.setText("");
            modelName.setText("<unknown>");
            this.icon.setIcon(null);
        }
        setBorder(isSelected ? selectedBorder : null);
        return this;
    }
}
