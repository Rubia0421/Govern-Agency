package com.govagency;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.govagency.model.Citizen;
import com.govagency.model.Document;
import com.govagency.model.ServiceRequest;

public class LocalDatabase {

    private static final String DB_PATH = "database.json";
    private final JSONObject root;

    public LocalDatabase() {
        root = load();
    }

    private JSONObject load() {
        try {
            if (!Files.exists(Path.of(DB_PATH))) {
                return initializeDatabase();
            }
            
            String text = Files.readString(Path.of(DB_PATH));
            JSONObject obj = new JSONObject(text);
            
            // Ensure all required keys exist
            if (!obj.has("citizens")) {
                obj.put("citizens", new JSONArray());
            }
            if (!obj.has("documents")) {
                obj.put("documents", new JSONArray());
            }
            if (!obj.has("requests")) {
                obj.put("requests", new JSONArray());
            }
            
            save(obj);
            return obj;
        } catch (IOException | org.json.JSONException e) {
            System.err.println("Error loading database: " + e.getMessage());
            return initializeDatabase();
        }
    }

    private JSONObject initializeDatabase() {
        JSONObject obj = new JSONObject();
        obj.put("citizens", new JSONArray());
        obj.put("documents", new JSONArray());
        obj.put("requests", new JSONArray());
        save(obj);
        return obj;
    }

    private void save(JSONObject data) {
        try (FileWriter fw = new FileWriter(DB_PATH)) {
            fw.write(data.toString(4));
        } catch (Exception e) {
            System.err.println("Error saving database: " + e.getMessage());
        }
    }

    // ------- CITIZENS -------
    public void addCitizen(Citizen c) {
        try {
            JSONArray arr = root.optJSONArray("citizens");
            if (arr == null) {
                arr = new JSONArray();
                root.put("citizens", arr);
            }

            JSONObject obj = new JSONObject();
            obj.put("id", c.getId());
            obj.put("name", c.getName());
            obj.put("email", c.getEmail());
            obj.put("number", c.getNumber());

            arr.put(obj);
            save(root);
        } catch (org.json.JSONException e) {
            System.err.println("Error adding citizen: " + e.getMessage());
        }
    }

    public void deleteCitizen(String citizenId) {
        try {
            JSONArray arr = root.optJSONArray("citizens");
            if (arr == null) return;
            
            JSONArray newArr = new JSONArray();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (!obj.getString("id").equals(citizenId)) {
                    newArr.put(obj);
                }
            }

            root.put("citizens", newArr);
            save(root);
        } catch (org.json.JSONException e) {
            System.err.println("Error deleting citizen: " + e.getMessage());
        }
    }

    public void updateCitizen(String citizenId, Citizen c) {
        try {
            JSONArray arr = root.optJSONArray("citizens");
            if (arr == null) return;

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.getString("id").equals(citizenId)) {
                    obj.put("name", c.getName());
                    obj.put("email", c.getEmail());
                    obj.put("number", c.getNumber());
                    break;
                }
            }

            save(root);
        } catch (org.json.JSONException e) {
            System.err.println("Error updating citizen: " + e.getMessage());
        }
    }

    // ------- DOCUMENTS -------
    public void addDocument(Document d) {
        try {
            JSONArray arr = root.optJSONArray("documents");
            if (arr == null) {
                arr = new JSONArray();
                root.put("documents", arr);
            }

            JSONObject obj = new JSONObject();
            obj.put("id", d.getId());
            obj.put("ownerId", d.getCitizenId());
            obj.put("status", d.getStatus().name());

            arr.put(obj);
            save(root);
        } catch (org.json.JSONException e) {
            System.err.println("Error adding document: " + e.getMessage());
        }
    }

    public void deleteDocument(String documentId) {
        try {
            JSONArray arr = root.optJSONArray("documents");
            if (arr == null) return;
            
            JSONArray newArr = new JSONArray();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (!obj.getString("id").equals(documentId)) {
                    newArr.put(obj);
                }
            }

            root.put("documents", newArr);
            save(root);
        } catch (org.json.JSONException e) {
            System.err.println("Error deleting document: " + e.getMessage());
        }
    }

    public void updateDocument(String documentId, Document d) {
        try {
            JSONArray arr = root.optJSONArray("documents");
            if (arr == null) return;

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.getString("id").equals(documentId)) {
                    obj.put("status", d.getStatus().name());
                    break;
                }
            }

            save(root);
        } catch (org.json.JSONException e) {
            System.err.println("Error updating document: " + e.getMessage());
        }
    }

    // ------- REQUESTS -------
    public void addRequest(ServiceRequest r) {
        try {
            JSONArray arr = root.optJSONArray("requests");
            if (arr == null) {
                arr = new JSONArray();
                root.put("requests", arr);
            }

            JSONObject obj = new JSONObject();
            obj.put("id", r.getId());
            obj.put("citizenId", r.getCitizenId());
            obj.put("type", r.getServiceType());
            obj.put("description", r.getDescription());
            obj.put("status", r.getStatus().name());
            obj.put("date", java.time.LocalDateTime.now().toString());

            arr.put(obj);
            save(root);
        } catch (org.json.JSONException e) {
            System.err.println("Error adding request: " + e.getMessage());
        }
    }

    public void deleteRequest(String requestId) {
        try {
            JSONArray arr = root.optJSONArray("requests");
            if (arr == null) return;
            
            JSONArray newArr = new JSONArray();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (!obj.getString("id").equals(requestId)) {
                    newArr.put(obj);
                }
            }

            root.put("requests", newArr);
            save(root);
        } catch (org.json.JSONException e) {
            System.err.println("Error deleting request: " + e.getMessage());
        }
    }

    public void updateRequest(String requestId, ServiceRequest r) {
        try {
            JSONArray arr = root.optJSONArray("requests");
            if (arr == null) return;

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.getString("id").equals(requestId)) {
                    obj.put("status", r.getStatus().name());
                    break;
                }
            }

            save(root);
        } catch (org.json.JSONException e) {
            System.err.println("Error updating request: " + e.getMessage());
        }
    }

    // -------- GETTERS --------
    public List<JSONObject> getAllCitizens() {
        JSONArray arr = root.optJSONArray("citizens");
        List<JSONObject> list = new ArrayList<>();
        
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                try {
                    list.add(arr.getJSONObject(i));
                } catch (org.json.JSONException e) {
                    System.err.println("Error reading citizen: " + e.getMessage());
                }
            }
        }
        
        return list;
    }

    public List<JSONObject> getAllDocuments() {
        JSONArray arr = root.optJSONArray("documents");
        List<JSONObject> list = new ArrayList<>();
        
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                try {
                    list.add(arr.getJSONObject(i));
                } catch (org.json.JSONException e) {
                    System.err.println("Error reading document: " + e.getMessage());
                }
            }
        }
        
        return list;
    }

    public List<JSONObject> getAllRequests() {
        JSONArray arr = root.optJSONArray("requests");
        List<JSONObject> list = new ArrayList<>();
        
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                try {
                    list.add(arr.getJSONObject(i));
                } catch (org.json.JSONException e) {
                    System.err.println("Error reading request: " + e.getMessage());
                }
            }
        }
        
        return list;
    }
}