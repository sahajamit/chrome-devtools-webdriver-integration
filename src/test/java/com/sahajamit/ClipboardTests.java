package com.sahajamit;

import com.sahajamit.utils.UINotificationService;
import com.sahajamit.utils.UIUtils;
import com.sahajamit.utils.Utils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClipboardTests {

    private WebDriver driver;
    private String wsURL;    private Utils utils;
    private UIUtils uiUtils;
    private ChromeDriverService chromeDriverService;

    @Before
    public void beforeTest(){
        this.utils = Utils.getInstance();
        this.uiUtils = UIUtils.getInstance();
    }

    @After
    public void afterTest(){
        utils.stopChrome();
        if(!Objects.isNull(chromeDriverService))
            chromeDriverService.stop();
    }
    @Test
    public void doLocalClipboardReadTest() throws Exception {
        String text = "Mussum ipsum cacilds, vidis litro abertis. Consetis adipiscings elitis. Pra lá , depois divoltis porris, paradis. Paisis, filhis, espiritis santis. Mé faiz elementum girarzis, nisi eros vermeio, in elementis mé pra quem é amistosis quis leo. Manduma pindureta quium dia nois paga.";
        driver = utils.launchBrowser();
        driver.navigate().to("https://googlechrome.github.io/samples/async-clipboard/");

        driver.findElement(By.cssSelector("textarea#out")).sendKeys(text);
        utils.waitFor(1);
        uiUtils.takeScreenShot();
        driver.findElement(By.cssSelector("button#copy")).click();
        utils.waitFor(1);
        String localClipboardData = (String) Toolkit.getDefaultToolkit()
                .getSystemClipboard().getData(DataFlavor.stringFlavor);
        Assert.assertEquals(text,localClipboardData);

    }

    @Test
    public void doRemoteClipboardReadTest() throws Exception {
        String text = "Mussum ipsum cacilds, vidis litro abertis. Consetis adipiscings elitis. Pra lá , depois divoltis porris, paradis. Paisis, filhis, espiritis santis. Mé faiz elementum girarzis, nisi eros vermeio, in elementis mé pra quem é amistosis quis leo. Manduma pindureta quium dia nois paga.";
        driver = utils.launchBrowser();
        driver.navigate().to("https://googlechrome.github.io/samples/async-clipboard/");

        driver.findElement(By.cssSelector("textarea#out")).sendKeys(text);
        utils.waitFor(1);
        uiUtils.takeScreenShot();
        driver.findElement(By.cssSelector("button#copy")).click();
        utils.waitFor(1);
        String localClipboardData = this.getCBContents();
        Assert.assertEquals(text,localClipboardData);
    }

    public String getCBContents(){
        UIUtils.getInstance().executeJavaScript("async function getCBContents() { try { window.cb = await navigator.clipboard.readText(); console.log(\"Pasted content: \", window.cb); } catch (err) { console.error(\"Failed to read clipboard contents: \", err); window.cb = \"Error : \" + err; } } getCBContents();");
        Object content = UIUtils.getInstance().executeJavaScript("return window.cb;");
        return Objects.isNull(content) ? "null" :  content.toString();
    }

}
