package media.framework;

import java.awt.Image;
import java.net.URI;


public class ImportFramework {
    
    /**
     * Book information we care about
     */
    public static interface BookData {
        String          title();
        String          author();
        String          blurb();

        String          text();
        
        Iterable<Image> images();
    }
    
    /**
     * Client API - What We Want To Use
     */
    public static interface LoaderImporter {
        
        boolean load(URI location);
        
        BookData data();
    }

    private ImportFramework() { }
}
