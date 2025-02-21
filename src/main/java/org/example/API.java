/*
 * Diese Datei wurde für die Aufgabe "Modul 165 - NoSQL-Datenbanken einsetzen - Aufgabe Chat" geschrieben.
 * Diese Klasse simuliert eine API Schnittstelle, welche Daten im JSON format an die Datenbank Schnittstelle schickt,
 * welche zu implementieren ist.
 *
 * Author: azu04
 * Datum 05.02.2025
 * */

package org.example; // todo: Eventuell package Pfad an eigene Projektstruktur anpassen

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public abstract class API {
     /**
     * Simuliert einen POST auf die API mit zufällig gewählten Daten. Die Daten werden in JSON formatiert
     * zurückgegeben.
     * Die Datenstruktur ist folgendermassen aufgebaut:
     * <pre>
     * {
     *   "message": string,
     *   "username": string,
     *   "attachments" : [
     *     { "name": string, "size": number }
     *   ]
     * }
     * </pre>
     **/
    public static String simulateRandomPost() {
        boolean postUserHasName = getRandomInt(10) < 3;
        boolean postHasMessage = getRandomInt(10) != 0;
        boolean postHasAttachments = getRandomInt(2) == 0 || !postHasMessage;


        String name = postUserHasName ? getRandomName() : null;
        String message = postHasMessage ? getRandomMessage(postHasAttachments) : null;
        List<HashMap<String, Object>> attachments = postHasAttachments ? getRandomAttachments(getRandomInt(5)) : null;

        return simulatePost(name, message, attachments);
    }

    /**
     * Simuliert einen POST auf die API mit vorgegebenen Daten. Die Daten werden in JSON formatiert
     * zurückgegeben.
     * Die Datenstruktur ist folgendermassen aufgebaut: <br/>
     * <pre>
     * {
     *   "message": string,
     *   "username": string,
     *   "attachments" : [
     *     { "name": string, "size": number }
     *   ]
     * }
     * </pre>
     *
     * @param name Name des users welcher die POST anforderung sendet. Ist der name leer, wird ein Zufälliger
     *             SHA-256 Hash als namen verwendet.
     * @param message Nachricht, welche der benutzer geschrieben hat.
     * @param attachments Liste an namen von Anhängen die an die Anforderung angehängt wurden.
     * */
    public static String simulatePost(String name, String message, List<HashMap<String, Object>> attachments) {
        if (name == null || name.isBlank()) {
            name = getRandomHashAsString();
        }
        if (message == null) {
            message = "";
        }
        if (attachments == null) {
            attachments = new ArrayList<>(0);
        }

        Map<String, Object> data = new HashMap<>(3);
        data.putIfAbsent("username", name);
        data.putIfAbsent("message", message);
        data.putIfAbsent("attachments", attachments);

        return parseToJSONObject(data);
    }

    /**
     * Generiert einen SHA-256 codierten Hash und gibt ihn als Base64 codierten String zurück. Diese Hash Funktion wird
     * für die Generation eines Namens für einen Benutzer genutzt, der beim Chat keinen Namen angegeben hat.
     * */
    public static String getRandomHashAsString() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.putLong(getRandomInt(10));
            MessageDigest digest = MessageDigest.getInstance("SHA256");
            byte[] hash = digest.digest(buffer.array());

            return Base64.getEncoder().encodeToString(hash);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace(System.err);
            return "HASH ALGORITHM 'SHA-256' NOT FOUND";
        }
    }

    /**
     * Gibt einen zufälligen Namen aus einer Liste zurück, welcher als Namen für eine simulierte POST
     * Anforderung verwendet werden kann.
     * */
    public static String getRandomName() {
        return PostData.userNames[getRandomInt(PostData.userNames.length)];
    }

    /**
     * Gibt eine zufällige Nachricht aus einer Liste zurück, welcher als Nachricht für eine simulierte POST
     * Anforderung verwendet werden kann.
     *
     * @param withAttachments Ob Nachrichteninhalt auf Anhänge referenziert.
     * */
    public static String getRandomMessage(boolean withAttachments) {
        if (withAttachments) {
            return PostData.messagesWithAttachments[getRandomInt(PostData.messagesWithAttachments.length)];
        } else {
            return PostData.messages[getRandomInt(PostData.messages.length)];
        }
    }

    /**
     * Gibt eine Liste mit zufälligen Namen für Anhänge zurück, welche als Anhänge für eine simulierte POST
     * Anforderung verwendet werden kann.
     *
     * @param amount Wie viele zufällige Anhänge die Liste beinhalten soll.
     * */
    public static List<HashMap<String, Object>> getRandomAttachments(int amount) {
        amount = Math.max(amount, 1);
        List<HashMap<String, Object>> attachments = new ArrayList<>(amount);

        for (int i = 0; i < amount; i++) {
            String name = String.format(
                    "%s_%s.%s",
                    PostData.attachmentNames[getRandomInt(PostData.attachmentNames.length)], i,
                    PostData.attachmentExtensions[getRandomInt(PostData.attachmentExtensions.length)]
            );
            Integer size = 200_000 + getRandomInt(5_000_000);

            attachments.add(createAttachment(name, size));
        }
        return attachments;
    }

    /**
     * Erstellt ein neues Attachment. Diese Attachments können dann mit <code>List.of()</code> zu einer Liste
     * konvertiert werden. Diese Attachment Liste kann als Anhänge für eine simulierte POST Anforderung verwendet
     * werden.
     *
     * @param name Name des attachments
     * @param size Grösse des Attachments in bytes
     * */
    public static HashMap<String, Object> createAttachment(String name, Integer size) {
        HashMap<String, Object> attachment = new HashMap<>(2);
        attachment.putIfAbsent("name", name);
        attachment.putIfAbsent("size", size);

        return attachment;
    }


    @SuppressWarnings("unchecked")
    private static String parseToJSONObject(Map<String, Object> data) {
        List<String> attributes = new ArrayList<>(data.size());

        data.forEach((k, v) -> {
            String value;
            if (v instanceof String) {
                value = String.format("\"%s\"", v);
            }
            else if (v instanceof Integer) {
                value = v.toString();
            }
            else {
                value = toJSONArray((List<HashMap<String, Object>>) v);
            }
            attributes.add(String.format("\"%s\":%s", k, value));
        });
        return String.format("{%s}", String.join(",", attributes.toArray(new String[0])));
    }

    private static String toJSONArray(List<HashMap<String, Object>> list) {
        String[] attachments = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            attachments[i] = parseToJSONObject(list.get(i));
        }
        return String.format("[%s]", String.join(",", attachments));
    }

    private static int getRandomInt(int limit) {
        return (int) (Math.random() * limit);
    }

    private static class PostData {
        private static final String[] attachmentExtensions = {
                "png", "jpg", "jpeg",
        };
        private static final String[] attachmentNames = {
                "image", "bild", "anhang", "screenshot", "foto", "Sofa", "Stuhl", "Sessel", "Tisch", "Schrank",
                "Nachttisch", "Bett", "Teppich", "Regal", "Stehlampe", "Deckenlampe",
        };
        private static final String[] userNames = {
                "Hans", "Peter Müller", "xX_NoscopePro360_Xx", "Yatze", "Achalam", "Sabine", "anon", "Herbert",
                "Silvan", "Tobias", "Andreas", "MinecraftLP_YT", "OzonTV_derechte", "SilverKnight",
        };
        private static final String[] messagesWithAttachments = {
                "Hallo, ich wollte nachfragen wie viel man für den versand zahlt",
                "Guten tag ich möhte mit Guschein kaufen",
                "Wann ist das hier wieder auf Lager?",
                "Hallo, ich habe das hier gerade bei Ihnen bestellt, aber es ist noch nicht angekommen",
                "Wie lange dauert die Lieferung dieses Produkts?",
                "Gibt es dieses Produkt noch?",
                "Haben Sie das noch auf lager?",
                "Ich möchte Auskunft über dieses Produkt",
                "Was genau ist das?",
                "Ich habe das hier gekauft",
                "Das möchte ich gerne zurückgeben",
                "Das produkt war beschädigt",
                "Wie viel kostet das?",
        };
        private static final String[] messages = {
                "Hallo?",
                "Wie funktioniert der Abwicklungsprozess?",
                "Kann man auch mit Twint Zahlen?",
                "Halo, ich dich beschützen wenn ich gutschein 100 billig bekome",
                "Alles klar, vielen dank für die Auskunft",
                "Abonniert meinen YOutube kanal!",
                "Guten Tag",
                "Ich habe ein Problem",
                "Können Sie mir helfen?",
                "Ich brauche unterstützung",
                "Ich möchte mich beschweren",
                "Ich habe ein Anliegen",
                "Sehr geehrte Damen und Herren",
                "Du bist doch nur ein Bot",
                "Achja ChatGPT nun auch auf dieser Seite",
                "Können Sie mir helfen?",
        };
    }
}

