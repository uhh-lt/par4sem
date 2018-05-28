package cwi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;


public class RemoveDup {
public static void main(String[] args) throws IOException {
	Set<String> uniql = new HashSet<>();
	LineIterator dupit = FileUtils.lineIterator(new File("src/main/resources/simplification/cwi_training/cw.tsv.lstm"));
	
	FileOutputStream os = new FileOutputStream(new File("src/main/resources/simplification/cwi_training/cw.tsv.lstm.unq"));
	while(dupit.hasNext()){
		String line = dupit.next();
		if(uniql.contains(line)){
			continue;
		}
		uniql.add(line);
		IOUtils.write(line+"\n", os,"UTF8");
	}
}
}
