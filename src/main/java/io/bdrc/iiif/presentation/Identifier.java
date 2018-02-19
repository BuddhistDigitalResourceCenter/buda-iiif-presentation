package io.bdrc.iiif.presentation;

import io.bdrc.iiif.presentation.exceptions.BDRCAPIException;
import static io.bdrc.iiif.presentation.AppConstants.*;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Identifier {
    public static final int MANIFEST_ID = 0;
    public static final int COLLECTION_ID = 1;
    
    public static final int COLLECTION_ID_ITEM = 2;
    public static final int COLLECTION_ID_WORK_IN_ITEM = 3;
    
    public static final int MANIFEST_ID_WORK_IN_ITEM = 4;
    public static final int MANIFEST_ID_VOLUMEID = 5;
    public static final int MANIFEST_ID_WORK_IN_VOLUMEID = 6;
    public static final int MANIFEST_ID_WORK_IN_ITEM_VOLNUM = 7;
    public static final int MANIFEST_ID_ITEM_VOLNUM = 8;
    
    @JsonProperty("id")
    String id = null;
    @JsonProperty("type")
    int type = -1;
    @JsonProperty("subtype")
    int subtype = -1;
    @JsonProperty("workId")
    String workId = null;
    @JsonProperty("ItemId")
    String itemId = null;
    @JsonProperty("volumeId")
    String volumeId = null;
    @JsonProperty("volNum")
    String volNum = null;
    
    public Identifier(final String iiifIdentifier, final int idType) throws BDRCAPIException {
        if (iiifIdentifier == null || iiifIdentifier.isEmpty())
            throw new BDRCAPIException(404, INVALID_IDENTIFIER_ERROR_CODE, "cannot parse identifier");
        final int firstColIndex = iiifIdentifier.indexOf(':');
        if (firstColIndex < 1)
            throw new BDRCAPIException(404, INVALID_IDENTIFIER_ERROR_CODE, "cannot parse identifier");
        final String typestr = iiifIdentifier.substring(0, firstColIndex);
        final String[] parts = iiifIdentifier.substring(firstColIndex+1).split("::");
        if (parts.length == 0 || parts.length > 3)
            throw new BDRCAPIException(404, INVALID_IDENTIFIER_ERROR_CODE, "cannot parse identifier");
        final String firstId = parts[0];
        if (firstId.isEmpty())
            throw new BDRCAPIException(404, INVALID_IDENTIFIER_ERROR_CODE, "cannot parse identifier");
        final String secondId = (parts.length > 1 && !parts[1].isEmpty()) ? parts[1] : null;
        final String thirdId = (parts.length > 2 && !parts[2].isEmpty()) ? parts[2] : null;
        int nbMaxPartsExpected = 0;
        this.id = iiifIdentifier;
        this.type = idType;
        if (idType == COLLECTION_ID) {
            switch (typestr) {
            case "i":
                this.itemId = firstId;
                nbMaxPartsExpected = 1;
                this.subtype = COLLECTION_ID_ITEM;
                break;
            case "wi":
                this.workId = firstId;
                this.itemId = secondId;
                nbMaxPartsExpected = 2;
                this.subtype = COLLECTION_ID_WORK_IN_ITEM;
                break;
            default:
                throw new BDRCAPIException(404, INVALID_IDENTIFIER_ERROR_CODE, "cannot parse identifier");
            }
            return;
        }
        // idType == MANIFEST_ID
        switch (typestr) {
        case "wi":
            this.workId = firstId;
            this.itemId = secondId;
            nbMaxPartsExpected = 2;
            this.subtype = MANIFEST_ID_WORK_IN_ITEM;
            break;
        case "v":
            this.volumeId = firstId;
            nbMaxPartsExpected = 1;
            this.subtype = MANIFEST_ID_VOLUMEID;
            break;
        case "wv":
            this.workId = firstId;
            this.volumeId = secondId;
            nbMaxPartsExpected = 2;
            this.subtype = MANIFEST_ID_WORK_IN_VOLUMEID;
            break;
        case "wivn":
            this.workId = firstId;
            if (secondId == null)
                throw new BDRCAPIException(404, INVALID_IDENTIFIER_ERROR_CODE, "cannot parse identifier");
            if (thirdId == null) {
                this.volNum = secondId;
//                try {
//                    this.volNum = Integer.parseInt(secondId);
//                } catch (NumberFormatException e) {
//                    throw new BDRCAPIException(404, INVALID_IDENTIFIER_ERROR_CODE, "cannot parse identifier");
//                }
            } else {
                this.itemId = secondId;
                this.volNum = thirdId;
//                try {
//                    this.volNum = Integer.parseInt(thirdId);
//                } catch (NumberFormatException e) {
//                    throw new BDRCAPIException(404, INVALID_IDENTIFIER_ERROR_CODE, "cannot parse identifier");
//                }
            }
            nbMaxPartsExpected = 3;
            this.subtype = MANIFEST_ID_WORK_IN_ITEM_VOLNUM;
            break;
        case "ivn":
            this.itemId = firstId;
            if (secondId == null)
                throw new BDRCAPIException(404, INVALID_IDENTIFIER_ERROR_CODE, "cannot parse identifier");
            this.volNum = secondId;
//            try {
//                this.volNum = Integer.parseInt(secondId);
//            } catch (NumberFormatException e) {
//                throw new BDRCAPIException(404, INVALID_IDENTIFIER_ERROR_CODE, "cannot parse identifier");
//            }
            nbMaxPartsExpected = 2;
            this.subtype = MANIFEST_ID_ITEM_VOLNUM;
            break;
        default:
            throw new BDRCAPIException(404, INVALID_IDENTIFIER_ERROR_CODE, "cannot parse identifier");
        }
        if (nbMaxPartsExpected < parts.length)
            throw new BDRCAPIException(404, INVALID_IDENTIFIER_ERROR_CODE, "cannot parse identifier");
    }
    
}
