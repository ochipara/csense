package edu.uiowa.csense.components.android.bluetooth;

import java.nio.ByteBuffer;

import edu.uiowa.csense.components.bluetooth.BluetoothCommand;

/*
 * Bluetooth client and server components should implement this interface to realize custom communication protocols.
 */
public interface BluetoothListener {
    /**
     * Invoked when a remote Bluetooth device is connected. Depending on the protocol, an initial request may be sent.
     * The implementation may be synchronized to avoid race condition.
     * @param service The Bluetooth client service representing the connection.
     */
    void onClientConnected(BluetoothClientService service);
    
    /**
     * Invoked when a response to a previous request command is read.
     * @param service The Bluetooth client service representing the connection.
     * @param cmd The previous request command to send.
     * @param response The response read.
     */
    void onResponse(BluetoothClientService service, BluetoothCommand cmd, ByteBuffer response);
    
    /**
     * Invoked when a Bluetooth client is going to stop. Some commands may need to be sent to end the communication gracefully.
     * @param service The Bluetooth client service representing the connection.
     */
    void onDisconnect(BluetoothClientService service);
}
