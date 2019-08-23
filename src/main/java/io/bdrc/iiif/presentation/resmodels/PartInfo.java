package io.bdrc.iiif.presentation.resmodels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PartInfo implements Comparable<PartInfo> {
    @JsonProperty("partIndex")
    public Integer partIndex = null;
    @JsonProperty("partId")
    public String partId = null;
    @JsonProperty("labels")
    public List<LangString> labels = null;
    @JsonProperty("subparts")
    public List<PartInfo> subparts = null;
    @JsonProperty("location")
    public Location location = null;
    @JsonProperty("linkTo")
    public String linkTo = null;
    @JsonProperty("linkToType")
    public String linkToType = null;
    
    public PartInfo(final String partId, final Integer partIndex) {
        this.partId = partId;
        this.partIndex = partIndex;
    }
    
    public PartInfo() {}
    
    @Override
    public int compareTo(PartInfo compared) {
        if (this.partIndex == null || compared.partIndex == null)
            return 0;
        return this.partIndex - compared.partIndex;
    }
}