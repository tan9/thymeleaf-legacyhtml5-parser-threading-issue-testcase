import java.io.InputStreamReader;
import java.io.Reader;

import org.testng.annotations.*;

import static org.testng.Assert.*;

import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templatemode.ITemplateModeHandler;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;
import org.thymeleaf.templatemode.TemplateModeHandler;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.html.LegacyHtml5TemplateParser;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class LegacyHtml5TemplateParserTest {

    public static final int POOL_SIZE = 3;

    private static Configuration configuration;

    @BeforeClass
    public static void setUp() {
        configuration = new Configuration();
        configuration.setTemplateResolver(new ClassLoaderTemplateResolver());
        configuration.setMessageResolver(new StandardMessageResolver());
        ITemplateModeHandler legacyHtml5Handler = StandardTemplateModeHandlers.LEGACYHTML5;

        ITemplateParser templateParser = new LegacyHtml5TemplateParser("LEGACYHTML5", POOL_SIZE);

        ITemplateModeHandler handler = new TemplateModeHandler(
                legacyHtml5Handler.getTemplateModeName(), templateParser,
                legacyHtml5Handler.getTemplateWriter());
        configuration.addTemplateModeHandler(handler);
        configuration.initialize();
    }

    @Test(threadPoolSize = 20, invocationCount = 20)
    public void testParsing() throws Exception {
        ITemplateParser parser = configuration.getTemplateModeHandler("LEGACYHTML5")
                .getTemplateParser();
        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("legacyhtml5-parser-threading-issue-testcase.html"))) {
            Document document = parser.parseTemplate(configuration, "name", reader);

            Node node = document.getChildren().get(0);
            System.out.println(node);

            assertTrue(node instanceof Element, "First node must be an instance of Element instead of Text");
        }
    }
}
