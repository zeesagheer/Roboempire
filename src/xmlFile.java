import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import pl.autoempire.core.gamefiles.Cloud;
import pl.autoempire.core.gamefiles.DungeonInfo;
import pl.autoempire.core.gamefiles.ItemsGameFile;
import pl.autoempire.core.gamefiles.ItemsVersionGameFile;

public class xmlFile {
    private final static String path="items.xml";
    public static ArrayList<DungeonInfo> dungeons = new ArrayList<DungeonInfo>();
	public static void load() throws Exception
	{
		final File file = new File(path);
		if (!file.exists())
			downloadFile();
        if (file.exists()) {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document doc = db.parse(file);
            final Element rootElement = doc.getDocumentElement();
            rootElement.normalize();
            if (!rootElement.getNodeName().equals("root")) {
                throw new Exception("No tag: root");
            }
            final NodeList dungeonList = ((Element)rootElement.getElementsByTagName("dungeons").item(0)).getElementsByTagName("dungeon");
    		for (int i = 0; i < dungeonList.getLength(); ++i) {
                dungeons.add(new DungeonInfo((Element)dungeonList.item(i)));
            }
        }
	}
	public static void downloadFile() throws IOException {
		final ItemsVersionGameFile itemsVersionFile = new ItemsVersionGameFile(Cloud.DEFAULT.getCloudInfo());
		String version;
        try {
            itemsVersionFile.loadFile(itemsVersionFile.downloadFile());
            version = itemsVersionFile.getCastleItemXMLVersion();
        }
        catch (IOException e1) {
            e1.printStackTrace();
            return;
        }
        catch (Exception e2) {
            e2.printStackTrace();
            return;
        }
        final ItemsGameFile gameFile = new ItemsGameFile(Cloud.DEFAULT.getCloudInfo(), version);
        
        try {
            final String fileName = gameFile.downloadFile();
            gameFile.loadFile(fileName);
            System.out.println("Items.xml loaded version :" + version);
        }
        catch (IOException e3) {
            e3.printStackTrace();
        }
        catch (Exception e4) {
            e4.printStackTrace();
        }
    }
}

