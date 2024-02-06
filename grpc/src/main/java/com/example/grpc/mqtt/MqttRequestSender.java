package com.example.grpc.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttRequestSender {

    public static void main(String[] args) {
        String broker = "tcp://test.mosquitto.org:1883";
        String clientId = "JavaMqttClient";
        String topic = "some_topic";

        try (MqttClient client = new MqttClient(broker, clientId)) {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            client.connect(options);

            String messageContent = "some message";
            int qos = 1;

            MqttMessage message = new MqttMessage(messageContent.getBytes());
            message.setQos(qos);

            client.publish(topic, message);

            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}