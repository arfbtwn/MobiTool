package media.framework;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFText2HTML;


public class PdfImporter extends AbstractImporter {
    
    static final byte[] SIG_PDF = "%PDF".getBytes();
    
    @Override
    public boolean doImport(byte[] data) {
        
        if (!testInput(data))
            return false;
        
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        
        try {
            PDDocument doc = PDDocument.load(bis);
            
            PDFText2HTML strip = new PDFText2HTML(Charset.defaultCharset().toString());
            
            String tmp = strip.getText(doc);
            
            text(tmp);
            
            doc.close();
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    boolean testInput(byte[] data) {
        
        for(int i=0; i<SIG_PDF.length; ++i)
            if (SIG_PDF[i] != data[i])
                return false;
        
        return true;
    }
    
}
