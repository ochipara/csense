package components.network;

import java.util.Map;

import messages.fixed.ByteVector;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDefaultFilePersistence;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import base.Utility;

public class MQTTClientComponent extends SourceComponent<ByteVector> {
    // public final Map<String, InPort<ByteVector>> PORTS_IN_FILE =
    // this.<ByteVector>setupInputPorts("in");
    // public final Map<String, OutPort<ByteVector>> PORTS_OUT_FILE =
    // this.<ByteVector>setupOutputPorts("out");
    public final IInPort<ByteVector> in = new InPort<ByteVector>(this, "in",
	    100);
    public final IOutPort<ByteVector> out = new OutPort<ByteVector>(this,
	    "out", 200);

    private final String _id;
    private final String _url;
    private final String _topic;
    private final int QoS = 2;
    private MqttClient _client;

    public MQTTClientComponent(String id, String host, int port)
	    throws CSenseException {
	this(id, host, port, null);
	addPort(in);
	addPort(out);
    }

    public MQTTClientComponent(String id, String host, int port, String topic)
	    throws CSenseException {
	_id = id;
	_url = Utility.toString("tcp://", host, ":", port);
	_topic = topic;
	setupMessagePool(256);

	String tmp = System.getProperty("java.io.tmpdir");
	MqttDefaultFilePersistence dataStore;
	try {
	    dataStore = new MqttDefaultFilePersistence(tmp);
	} catch (MqttPersistenceException e) {
	    e.printStackTrace();
	    throw new CSenseException(
		    "failed to create the default file persistence", e);
	}
	// Construct the object that contains connection parameters
	// such as cleansession and LWAT
	// conOpt = new MqttConnectOptions();
	// conOpt.setCleanSession(false);
	try {
	    _client = new MqttClient(_url, _id, dataStore);
	    _client.setCallback(new MqttCallback() {
		@Override
		public void connectionLost(Throwable cause) {
		    Log.w(this, "connection to %s lost, retry", _url);
		    try {
			_client.connect();
		    } catch (MqttSecurityException e) {
			e.printStackTrace();
		    } catch (MqttException e) {
			e.printStackTrace();
		    }
		}

		@Override
		public void deliveryComplete(MqttDeliveryToken token) {
		    Log.i(this, "MQTT message sent");
		}

		@Override
		public void messageArrived(MqttTopic topic, MqttMessage message)
			throws Exception {
		    String text = new String(message.getPayload());
		    Log.i(this, "received topic %s, size %d, message '%s'",
			    topic.toString(), message.getPayload().length, text);
		    ByteVector msg = getNextMessageToWriteInto();
		    msg.put(message.getPayload());
		    msg.flip();
		    getOutputPort("out").push(msg);
		}
	    });
	} catch (MqttException e) {
	    e.printStackTrace();
	    throw new CSenseException(
		    "failed to instantiate an MQTT client or set its callback",
		    e);
	}
    }

    /**
     * Performs a single publish
     * 
     * @param topicName
     *            the topic to publish to
     * @param qos
     *            the QoS to publish at
     * @param payload
     *            the payload of the message to publish
     * @throws MqttException
     */
    private void publish(String topicTitle, int qos, byte[] payload)
	    throws MqttException {
	MqttTopic topic = _client.getTopic(topicTitle);
	MqttMessage message = new MqttMessage(payload);
	message.setQos(qos);
	Log.i(this, "publishing to topic %s with QoS %d", topic, qos);
	topic.publish(message);
    }

    /**
     * Subscribes to a topic and blocks until Enter is pressed
     * 
     * @param topic
     *            the topic to subscribe to
     * @param qos
     *            the QoS to subscibe at
     * @throws MqttException
     */
    public void subscribe(String topic, int qos) throws MqttException {
	Log.i(this, "subscribing to topic %s with QoS %d", topic, qos);
	_client.subscribe(topic, qos);
    }

    @Override
    protected void doInput(InPort<? extends Message> port) {
	ByteVector msg = in.poll();
	String path = new String(msg.bytes());
	byte[] payload = new byte[msg.remaining()];
	msg.get(payload);
	try {
	    Log.i(this, "publishing message '%s' of %d bytes", path,
		    payload.length);
	    publish("egosense/speaker", QoS, payload);
	} catch (MqttException e) {
	    e.printStackTrace();
	}
	out.push(msg);
    }

    @Override
    public void activate() throws CSenseException {
	try {
	    _client.connect();
	    Log.i(this, "connected to %s", _url);
	    if (_topic != null) {
		subscribe(_topic, QoS);
		Log.i(this, "subscribing topic '%s'", _topic);
	    }
	} catch (MqttException e) {
	    throw new CSenseException(e);
	}
    }

    @Override
    public void deactivate() throws CSenseException {
	if (_client.isConnected()) {
	    try {
		_client.disconnect();
	    } catch (MqttException e) {
		e.printStackTrace();
	    }
	    Log.i(this, "disconnected");
	}
    }

    @Override
    protected boolean mayPull(OutPort<? extends Message> port) {
	return getInputPort().pull();
    }
}
