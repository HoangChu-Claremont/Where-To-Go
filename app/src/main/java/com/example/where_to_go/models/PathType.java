package com.example.where_to_go.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.parse.ParseFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PathType {

    private String pathTypeName;
    private String pathTypeImageUrl;

    public PathType (String typeName, String typeImageUrl) {
        pathTypeName = typeName;
        pathTypeImageUrl = typeImageUrl;
    }

    // GETTER
    public String getPathTypeName() {
        return pathTypeName;
    }

    public String getPathTypeImageUrl() {
        return pathTypeImageUrl;
    }
}
