package cn.com.nettex.apps;

import cn.com.nettex.apps.impl.txt.TextReader;
import cn.com.nettex.apps.impl.xsl.ExcelWriter;
import javafx.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void readText() {
        String srcPath = "L:\\Cache\\Data\\HENGSHEN-TEXTURMATMEP-DATA-20171128\\Kmgmep\\Data\\1576\\TeSiVa.1";
        String desPath = "N:\\AppData\\Local\\Temp\\Conv\\TeSiVa.1.xlsx";
        TextReader reader = new TextReader();
        ExcelWriter writer = new ExcelWriter();
        try {
            List<Pair<List<String>, List<List<Object>>>> result = reader.read(srcPath, false);
            Optional.ofNullable(result).ifPresent(pairs -> {
                pairs.forEach(pair -> {
                    try {
                        writer.write(pair, desPath);
                    } catch (IOException e) {
                        System.out.println(e.getCause().getMessage());
                    }
                });
                pairs.forEach(pair -> {
                    System.out.println(pair.getKey());
                    System.out.println(pair.getValue());
                });
            });
        } catch (IOException e) {
            System.err.println(e.getCause().getMessage());
        }
    }
}
