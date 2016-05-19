package com.example.admin.newnews.findMadels;

/**
 * Created by Admin on 11/25/2015.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "query",
        "entries"
})
public class ResponseData {

    @JsonProperty("query")
    private String query;
    @JsonProperty("entries")
    private List<Entry> entries = new ArrayList<Entry>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The query
     */
    @JsonProperty("query")
    public String getQuery() {
        return query;
    }

    /**
     * @param query The query
     */
    @JsonProperty("query")
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * @return The entries
     */
    @JsonProperty("entries")
    public List<Entry> getEntries() {
        return entries;
    }

    /**
     * @param entries The entries
     */
    @JsonProperty("entries")
    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
