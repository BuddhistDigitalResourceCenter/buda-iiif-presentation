package io.bdrc.iiif.presentation;

import static io.bdrc.iiif.presentation.AppConstants.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import de.digitalcollections.iiif.model.ImageContent;
import de.digitalcollections.iiif.model.PropertyValue;
import de.digitalcollections.iiif.model.enums.ViewingDirection;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import de.digitalcollections.iiif.model.sharedcanvas.Sequence;
import io.bdrc.iiif.presentation.exceptions.BDRCAPIException;

public class ManifestService {

    public static String getLabelFormImage(final int imageIndex) {
        if (imageIndex < 2)
            return "tbrc-"+(imageIndex+1);
        return "p. "+(imageIndex-1);
    }
    
    public static String getImageServiceUrl(final String filename, final Identifier id) {
        return "https://images.bdrc.io/iiif/2/"+id.itemId+"::"+filename;
    }
    
    public static Manifest getManifestForIdentifier(final Identifier id) throws BDRCAPIException {
        final Manifest manifest = new Manifest("http://presentation.bdrc.io/2.1.1/"+id.id+"/manifest", "This is a label");
        final PropertyValue attr = new PropertyValue();
        attr.addValue(Locale.ENGLISH, "Buddhist Digital Resource Center");
        manifest.setAttribution(attr);
        System.out.println(manifest.CONTEXT);
        manifest.addLicense("https://creativecommons.org/publicdomain/mark/1.0/");
        //manifest.addLogo("https://www.tbrc.org/safari-pinned-tab.svg");
        manifest.setViewingDirection(ViewingDirection.TOP_TO_BOTTOM);
        final Sequence seq = new Sequence("http://presentation.bdrc.io/2.1.1/"+id.id+"/sequence");
        manifest.addSequence(seq);
        List<ImageInfo> imageInfoList = ImageInfoListService.getImageInfoList(id);
        for (int i = 0; i < imageInfoList.size(); i++) {
            final ImageInfo imageInfo = imageInfoList.get(i);
            final String label = getLabelFormImage(i);
            final String canvasUri = "http://presentation.bdrc.io/2.1.1/"+id.id+"/canvas/"+(i+1);
            final Canvas canvas = new Canvas(canvasUri, label);
            canvas.setWidth(imageInfo.width);
            canvas.setHeight(imageInfo.height);
            final String imageServiceUrl = getImageServiceUrl(imageInfo.filename, id);
            //canvas.addIIIFImage(imageServiceUrl, ImageApiProfile.LEVEL_ONE);
            ImageService imgServ = new ImageService(imageServiceUrl, ImageApiProfile.LEVEL_ZERO);
            ImageContent img = new ImageContent(imgServ);
            img.addService(imgServ);
            img.setWidth(imageInfo.width);
            img.setHeight(imageInfo.height);
            canvas.addImage(img);
            seq.addCanvas(canvas);
            if (i == 0) {
                try {
                    seq.setStartCanvas(new URI(canvasUri));
                } catch (URISyntaxException e) { // completely stupid but necessary
                    throw new BDRCAPIException(500, GENERIC_APP_ERROR_CODE, e);
                }
            }
        }
        return manifest;
    }
    
}
