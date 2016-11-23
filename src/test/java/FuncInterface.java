import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.nio.file.Path;


interface FuncInterface {

   void GetText(String webElement, String compareValue, WebDriver driver);

   int pageNumber (String CSSselector, WebDriver driver);

   void GetTextPresentAlert(String CSSwebElement, String compareValue, WebDriver driver);

   void inputValue(WebDriver driver, String xpath, String inputValue ) throws InterruptedException;

   void inputMultiArticles(WebDriver driver, String inputBuffer) throws InterruptedException;

   void waitForElementByName (String elementName, WebDriver driver) throws RuntimeException;

   void waitForElementByXpath (String elementXpath, int waitTime, WebDriver driver) throws InterruptedException;


    //================ Element CHECK =========================

    void isFieldEmpty(String elmentName, WebDriver driver);

    void isElementEnabledAlert(String elementName,WebDriver driver);

    void isElementEnabledAlertByXpath(String elementXpath,WebDriver driver);

    void isElementDisabledAlertByXpath(String elementXpath,WebDriver driver);

    void pageCheckForValue(String searchValue, WebDriver driver);

    void playListCheckForClear(String searchValue, WebDriver driver);

    void checkPlayListPagesForValues(WebDriver driver, String searchValue) throws InterruptedException;

    String getFieldTextByXpath(WebDriver driver, String xPath) throws InterruptedException;

    void checkForDeskTopOpen(WebDriver driver, String xPath);

    void checkForDeskTopClose(WebDriver driver, String xPath);

    void checkContainsValueByXpath (WebDriver driver,String xPath, String searchValue) throws InterruptedException;

    void checkForInfoBlockEnabled(WebDriver driver);

    void checkForInfoBlockDisabled (WebDriver driver);



    void openPopUpWindow (WebDriver driver, String baseUrl,
                          String userLogin, String userPassword, String currentDomain) throws Exception;

    void clearPopUp(WebDriver driver) throws Exception;

    void searchProductList(String searchValue, WebDriver driver) throws InterruptedException;

    void searchProvider(String searchValue, WebDriver driver) throws InterruptedException;

    void getSearchArticle(String searchArticulNum, WebDriver driver) throws InterruptedException;

    int returnDate (int date);

    void openDesktopWithArticleByXpath(WebDriver driver, String desktopXpath,
                                       String searchProvider, String desctopName, String baseUrl,
                                       String userLogin, String userPassword, String currentDomain) throws Exception;

    void OkHTTP_request_TFS_ADD_task(String steckTrace, String errorMessage, String testName)throws IOException;


    void clickSelectorByXpath(WebDriver driver, String xPath, String errorMessage);

    void clickSelectorByClass(WebDriver driver, String classPath, String errorMessage);

    void sendKeysByXpath(WebDriver driver, String xPath, String errorMessage, String sendKeysVal) throws InterruptedException;

    void checkWorkDesktopAttribute(WebDriver driver, String deskTopAttribute);

    void verifyForecastDesktop(WebDriver driver);

    void checkSumForecastDeskTop (WebDriver driver) throws InterruptedException;

    void checkBoxDisabledAlertByXpath(WebDriver driver, String xPath) throws InterruptedException;

    void catchStatusLoadPage() throws IOException;

    void getOffDefaultGraphics(WebDriver driver) throws InterruptedException;

    void openReportDeskTop(WebDriver driver) throws InterruptedException;

    boolean hasClass(WebElement element, String htmlClass);

    void isFileExist(String fileName, Path downloadFilepath);

    void switchToAnalyticReport (WebDriver driver) throws InterruptedException;

    boolean ifElementExist(String xPath, String elementName, WebDriver driver);

    void loginUser (String userLogin, String userPassword, String domain, WebDriver driver) throws IOException, InterruptedException;

    void testLoginUser (String userLogin, String userPassword, String domain, WebDriver driver) throws IOException, InterruptedException;

    void checkRequiredField (WebDriver driver, String fieldXPath) throws RuntimeException;
}
