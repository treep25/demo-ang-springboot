package com.example.grpc.mqtt;

import org.eclipse.paho.client.mqttv3.*;

public class MqttRequestListener {

    public static void main(String[] args) {
        String broker = "tcp://test.mosquitto.org:1883";
        String clientId = "listener";
        String topic = "some_topic";

        try (MqttClient mqttClient = new MqttClient(broker, clientId)) {

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println(new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });
            mqttClient.connect();
            mqttClient.subscribe(topic);


            System.out.println("Listening for messages on topic: " + topic);
            while (true) {
                Thread.sleep(1000); // Пауза для предотвращения завершения приложения
            }

        } catch (MqttException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Interrupted: " + e.getMessage());
        }
    }
}
