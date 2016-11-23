import com.google.common.collect.Lists;
import okhttp3.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.StringWriter;
import java.net.*;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import okhttp3.Request;
import okhttp3.Interceptor;
import javax.json.*;


/** Клас з набором методів що використовуються в тестах
 * Клас імплементується від інтерфейса FuncInterface
 */
class Utilities implements FuncInterface, Interceptor {

    private String credentials;

    /** Метод для отримання та порівняння текстового знячення елемента з вказаним значенням, що знаходиться по CSS
     * @param CSSwebElement - текстове значення шляху до елемента CSS
     * @param compareValue - величина для порівняння
     * @param driver - об'єкт вебдрайвера
     */
    public void GetText(String CSSwebElement, String compareValue, WebDriver driver) {
        final List<WebElement> elements = driver.findElements(By.cssSelector(CSSwebElement));
        assertTrue(String.format("Не можливо знайти шукану величину: %s в плейлісті",compareValue), elements
                .stream().allMatch(e -> e.getText().toLowerCase().contains(compareValue.toLowerCase())));
    }

    /** Метод для перевірки наявності наявності текстового значення в певному елементі
     * Даний метод видає повідомлення про помилку в разі знаходження шуканого значення
     * @param CSSwebElement - текстове значення шляху до елемента CSS
     * @param compareValue - величина для порівняння
     * @param driver - об'єкт вебдрайвера
     */
    public void GetTextPresentAlert(String CSSwebElement, String compareValue, WebDriver driver) {
        final List<WebElement> elements = driver.findElements(By.cssSelector(CSSwebElement));
       assertFalse(String.format("Даний текст:%s - не має відображатись",compareValue), elements.stream()
               .allMatch(e -> e.getText().toLowerCase().contains(compareValue.toLowerCase())));
    }

    /** Метод для отримання кількості сторінок знайдених артикулів
     * метод отримує значення більше 1 сторінки
     * @param CSSselector - текстове значення шляху до елемента CSS
     * @param driver - об'єкт вебдрайвера
     * @return - в разі успіху - повертається числове значення к-сті сторінок знайдених результатів;
     * у разі провалу, повертається "".
     */
    public int pageNumber (String CSSselector, WebDriver driver){
        final List<WebElement> wraperList = driver.findElements(By.cssSelector(CSSselector));
        assertTrue(String.format
                ("Не знайдено елемент: %s - номер сторінки, або ксть. < 2",CSSselector),wraperList.size() > 2);
        List<String> pageNumbers = Lists.transform(wraperList, e -> {
            if (e != null)
                return e.getText();
            else
                return "";
        });
        return Integer.parseInt(pageNumbers.get(pageNumbers.size() -1));
    }


    //========================= Element CHECK =========================

    /** Метод здійснює перевірку на очиску певного поля
     * В разі знаходження певної величини в полі буде викинуто RuntimeException
     * @param elementName - назва поля
     * @param driver - об'єкт вебдрайвера
     */
    public void isFieldEmpty(String elementName, WebDriver driver){
        WebElement textBoxContent;
        try {
            textBoxContent = driver.findElement(By.name(elementName));
        } catch (Exception e){
            throw new RuntimeException(String.format("не знайдено елемент з іменем: %s", elementName));
        }

        if(!textBoxContent.getAttribute("value").equalsIgnoreCase("")){
            throw new RuntimeException(String.format("Поле %s не очищено!", elementName));
        }
    }

    /** Перевірка стану елемента на неактивність по імені
     * в разі коли елемент активний - буде викинуто RuntimeException
     * @param elementName - текстовий ім'я елемента
     * @param driver - об'єкт вебдрайвера
     */
    public void isElementEnabledAlert(String elementName,WebDriver driver){
        if(driver.findElement(By.name(elementName)).isEnabled()){
            throw new RuntimeException(String.format("Елемент: %s активний !", elementName));
        }
    }

    /** Перевірка стану елемента на неактивність по Xpath
     * в разі коли елемент активний - буде викинуто RuntimeException
     * @param elementXpath - текстовий шлях для знаходження елемента
     * @param driver - об'єкт вебдрайвера
     */
    public void isElementEnabledAlertByXpath(String elementXpath,WebDriver driver){
        WebElement elementButtonSelect = driver.findElement(By.xpath(elementXpath));
        if(elementButtonSelect.isEnabled()) {
            throw new RuntimeException(String.format("Елемент %s активний!", elementXpath));
        }
    }

    /** Перевірка стану елемента на активність по Xpath
     * в разі коли елемент неактивний - буде викинуто RuntimeException
     * @param elementXpath - текстовий шлях для знаходження елемента
     * @param driver - об'єкт вебдрайвера
     */
    public void isElementDisabledAlertByXpath(String elementXpath,WebDriver driver){
        WebElement elementButtonSelect;
        try {
            elementButtonSelect = driver.findElement(By.xpath(elementXpath));
        } catch (Exception e){
            throw new RuntimeException(String.format("не знайдено елемент з іменем: %s", elementXpath));
        }

        if(!elementButtonSelect.isEnabled()) {
            throw new RuntimeException(String.format("Елемент %s не активний!", elementXpath));
        }
    }

    /** Метод для перевірки наявності текстової величини на сторінці
     * в разі виявлення значення в DOM-сторінки буде викинуто повідомлення
     * @param searchValue - шукана текстова величина
     * @param driver - об'єкт вебдрайвера
     */
    public void  pageCheckForValue(String searchValue, WebDriver driver) {
        if(!(driver.getPageSource().contains(searchValue))){
            throw new RuntimeException(String.format("Інформація %s не знайдена!",searchValue ));
        }
    }

    /** Метод для перевірки очистки плейліста
     * методом відбувається пошук вказаної величини на наявність її в плейлісті
     * @param searchValue - шукана величина
     * @param driver - об'єкт вебдрайвера
     */
    public void playListCheckForClear(String searchValue, WebDriver driver) {
        if((driver.getPageSource().contains(searchValue))){
            throw new RuntimeException(String
                    .format("Плей ліст не очищено. шукана величина: %s залишилась!", searchValue));
        }
    }

    /** Метод для перевірки кожної сторінки плейліста (в разі коли таких багато) на наявність шуканої величини
     * більше використовується в разі пошуку по назві товару
     * @param driver - об'єкт вебдрайвера
     * @param searchValue - шукана величина
     * @throws InterruptedException - в разі наявності лише одної сторінки повертається значення 2-ки для зручної обробки масива
     */
    public void checkPlayListPagesForValues(WebDriver driver, String searchValue) throws InterruptedException {
        /**
         * змінна к-сті сторінок
         */
        int pageNumber;

        try {
            pageNumber = pageNumber(".item-pagination.ng-binding",driver);
        } catch (AssertionError e){
            pageNumber = 2;
        }

        clickSelectorByClass(driver,"fa-angle-double-left","Не можливо знайти кнопку переключення до першої сторінки");
        for(int i=0; i < pageNumber; i++){
            clickSelectorByClass(driver,"fa-chevron-right","Не можливо знайти кнопку переключення до слідуючої сторінки");
            Thread.sleep(1500);
            GetText(".list-provider-name.ng-binding", searchValue, driver);
        }
    }

    /** Отримання текстового значення елемента по xPath
     * @param driver - об'єкт вебдрайвера
     * @param xPath - шлях до елемента
     * @return - текстове значення елемента
     * @throws InterruptedException - помилка в разі неможливості доступитись до поля або поле пусте
     */
    public String getFieldTextByXpath(WebDriver driver, String xPath) throws InterruptedException {
        String getFieldTextValue;
        Thread.sleep(2500);
        try {
            getFieldTextValue = driver.findElement(By.xpath(xPath)).getText();
        } catch (Exception e){
            throw new RuntimeException(String.format("Не можливо звернутись до поля %s, панелі фільтрів", xPath));
        }
        if(getFieldTextValue.equalsIgnoreCase("")){
            throw new RuntimeException(String.format("Поле %s пусте", xPath));
        }
        return getFieldTextValue;
    }

    /** Метод здійснює перевірку робочого столу на те чи він відкрився
     * @param driver - об'єкт вебдрайвера
     * @param xPath - шлях до робочого столу
     */
    public void checkForDeskTopOpen(WebDriver driver, String xPath){
        try {
            driver.findElement(By.xpath(xPath)).isDisplayed();
        } catch (Exception e){
            throw new RuntimeException(String.format("Не можливо відкрити робочий стіл: %s", xPath));
        }
    }

    /** Метод здійснює перевірку робочого столу на те чи він закритий
     * @param driver - об'єкт вебдрайвера
     * @param xPath - шлях до елемента
     */
    public void checkForDeskTopClose(WebDriver driver, String xPath){
        Boolean isDisplayed;
        isDisplayed = !driver.findElements(By.xpath(xPath)).isEmpty();

        if(isDisplayed){
            throw new RuntimeException(String.format("Робочий стіл: %s не повинен відображатись",xPath));
        }

    }

    /** перевірка елемента по xPath на вміст  шуканої величини
     * @param driver - об'єкт вебдрайвера
     * @param xPath - шлях до елемента
     * @param searchValue - шукана величина
     * @throws InterruptedException - помилка в разі коли не можливо отримати вміст елемента
       або шуканої величини не знайдено
     */
    public void checkContainsValueByXpath (WebDriver driver,String xPath, String searchValue) throws InterruptedException {

        if(!getFieldTextByXpath(driver, xPath).toLowerCase().contains(searchValue.toLowerCase())){
            throw new RuntimeException(String
                    .format("Вибране поле:%s не містить шуконої величини:%s ", xPath, searchValue));
        }
    }

    /**Метод для перевірки на активність інфоблоку
     * перевірка здійснюється за наявності класу
     * @param driver - об'єкт вебдрайвера
     */
    public void checkForInfoBlockEnabled(WebDriver driver){
        boolean elementStatus;
        elementStatus = driver.findElements(By.cssSelector(".info-wrap.active")).isEmpty();
        if (elementStatus) {
            throw new RuntimeException("Інфоблок не активний!");
        }
    }

    /**Метод для перевірки на неактивність інфоблоку
     * перевірка здійснюється за наявності класу
     * @param driver - об'єкт вебдрайвера
     */
    public void checkForInfoBlockDisabled (WebDriver driver){
        boolean elementStatus;
        elementStatus = driver.findElements(By.cssSelector(".info-wrap.active")).isEmpty();
        if (!elementStatus) {
            throw new RuntimeException("Інфоблок активний!");
        }
    }

    //================ Element ACTION =========================

    /**Відкриття поп-ап вікна
     * @param driver - об'єкт вебдрайвера
     * @throws Exception - помилка в разі неможливості клікнути на елемент артикул панелі фільтрів або якщо поп-ап вікно
     * не увімкнулось
     */
    public void openPopUpWindow (WebDriver driver, String baseUrl,
                                 String userLogin, String userPassword, String currentDomain) throws Exception{
        catchStatusLoadPage();
        driver.get(baseUrl);

        String currentUrlVal;
        try {
            currentUrlVal = driver.getCurrentUrl();
        } catch (Exception e){
            throw new RuntimeException("Не можливо отримати поточного значення URL");
        }

        if(currentUrlVal.equals("http://crosstestxxxxxxxxxx/SalesForecast/#/login")){
            loginUser(userLogin, userPassword, currentDomain, driver);
        }

        WebElement dynamicElement = (new WebDriverWait(driver, 15))
                .until(ExpectedConditions.presenceOfElementLocated(By.className("article-cell")));
        if(dynamicElement == null){
            throw new RuntimeException("Не можливо здійснити очікування елемента Артикул");
        }
        clickSelectorByClass(driver,"article-cell","Не можливо відкрити POP UP меню");
        Thread.sleep(1500);
    }

    /**Очистка поп-ап вікна
     * реалізована через натискання на кнопку Очистити
     * @param driver - об'єкт вебдрайвера
     * @throws Exception - помилка, якщо не можливо здійснити клік на кнопку очистити
     */
    public void clearPopUp(WebDriver driver) throws Exception{
       clickSelectorByXpath(driver,"//div[3]/button[1]","Не можливо натиснути кнопку Очистити");
    }

    /**Пошук товару по назві товару
     * Вікно поп-ап повинно бути відкрите
     * @param searchValue - назва товару
     * @param driver - об'єкт вебдрайвера
     * @throws InterruptedException помилка, в разі коли не можливо здійснити пошук товару по назві
     */
    public void searchProductList(String searchValue, WebDriver driver) throws InterruptedException {
        Thread.sleep(1500);
        sendKeysByXpath(driver,"//form/div[1]/div[1]/input","Не можливо ввести текст в поле Назва товару",searchValue);
        clickSelectorByXpath(driver,"//div[3]/button[2]","Не можливо натиснути кнопку Відібрати POP UP меню");
        Thread.sleep(4500);
        System.gc();
    }

    /**Пошук постачальника в поп-ап вікні
     * попап вікно повинне бути відкрите
     * @param searchValue - текстове значення постачальника
     * @param driver - об'єкт вебдрайвера
     * @throws InterruptedException - в разі не можливості знайти постачальника
     */
    public void searchProvider(String searchValue, WebDriver driver) throws InterruptedException {
        sendKeysByXpath(driver,"//div[3]/div/input","Не можливо ввести текст в поле Постачальник", searchValue);
        isElementDisabledAlertByXpath("//div[3]/div/button",driver);
        clickSelectorByXpath(driver,"//div[3]/div/button","Не можливо натиснути кнопку (...)");
        Thread.sleep(5500);
    }

    /**Загальний метод для введення величини в будь-яке поле
     * @param driver - об'єкт вебдрайвера
     * @param xpath - шлях до текстового поля
     * @param inputValue - значення що буде вводитись
     * @throws InterruptedException - Повідомлення про неможливість введення значення в текстове поле
     */
    public void inputValue(WebDriver driver, String xpath, String inputValue ) throws InterruptedException {
        sendKeysByXpath(driver,xpath,String.format("Не можливо ввести велечину в поле %s",xpath),inputValue);
        Thread.sleep(1500);
        System.gc();
    }

    /**Метод для здійснення пошуку масиву артикулів
     * Поп-ап вікно повиненне бути відкритим
     * @param driver - об'єкт вебдрайвера
     * @param inputBuffer - текстовий масив артикулів, по яким здійсниться пошук
     * @throws InterruptedException - в разі неможливості здійснити пошук масиву артикулів - помилка
     */
    public void inputMultiArticles(WebDriver driver, String inputBuffer) throws InterruptedException {
        // button "..."
        waitForElementByXpath("//div/div/div/div/div", 15, driver);
        clickSelectorByXpath(driver,"//div/div/div/div/div","Не можливо здійснити клік на кнопку (...)");

        //paste buffer
        StringSelection selection = new StringSelection(inputBuffer);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        try {
            driver.findElement(By.xpath("//textarea")).sendKeys(Keys.chord(Keys.CONTROL, "v"));
        }catch (Exception e){
            throw new RuntimeException("Не можливо здійснити вставку з буфера CTRL+V");
        }

        //klick OK
        clickSelectorByXpath(driver,"//div[3]/button","Не можливо натиснути кнопку ОК форми Мультівставка");
        if(!(driver.getPageSource().contains("Масив артикулів"))){
            throw new RuntimeException("Не можливо задати масив артикулів!");
        }
        System.gc();
    }

    /**Здійснення очікування елемента по імені
     * @param elementName - ім'я елемента
     * @param driver - об'єкт вебдрайвера
     * @throws RuntimeException - в разі не можливості здійснити очікування - помилка
     */
    public void waitForElementByName (String elementName, WebDriver driver) throws RuntimeException {
        try {
            WebElement dynamicElement = (new WebDriverWait(driver, 15))
                    .until(ExpectedConditions.elementToBeClickable(By.name(elementName)));
            if(dynamicElement == null){
                throw new RuntimeException("не можливо отримати об'єкт веб елемента");
            }
        }catch (Exception e){
            throw new RuntimeException(String.format("Не можливо здiйснити очікування елемента %s", elementName));
        }

    }

    /**Здійснення очікування елемента по Xpath
     * @param elementXpath - Xpath елемента
     * @param waitTime час очікування елемента
     * @param driver - об'єкт вебдрайвера
     * @throws RuntimeException - в разі не можливості здійснити очікування - помилка
     */
    public void waitForElementByXpath (String elementXpath, int waitTime, WebDriver driver) throws InterruptedException {

        Boolean spinner = driver.findElements(By
                .cssSelector("body > div.spinner > div.overlay > div.large-spinner")).size() > 0;
        int countRetry = 0;

        while (spinner && countRetry < 40){
            Thread.sleep(1000);
            spinner = driver.findElements(By
                    .cssSelector("body > div.spinner > div.overlay > div.large-spinner")).size() > 0;

            countRetry += 1;

            if(spinner && countRetry == 40){
                throw new RuntimeException("Спінер залишається видимим більше 40 секунд");
            }
        }

        try {
            WebElement dynamicElement = (new WebDriverWait(driver, waitTime))
                    .until(ExpectedConditions.elementToBeClickable(By.xpath(elementXpath)));
            if(dynamicElement == null){
                throw new RuntimeException("не можливо отримати об'єкт веб елемента");
            }
        }catch (Exception e){
            try {
                WebElement dynamicElement = (new WebDriverWait(driver, waitTime))
                        .until(ExpectedConditions.presenceOfElementLocated(By.xpath(elementXpath)));
                if(dynamicElement == null){
                    throw new RuntimeException("не можливо отримати об'єкт веб елемента");
                }
            } catch (Exception ex){
                throw new RuntimeException(String.format("Не можливо здiйснити очікування елемента: %s", elementXpath));
            }
        }
    }

    /**Здійснення пошкуку артикула
     * повна стадія - від початку до виведення його на панелі фільтрів
     * @param searchArticulNum - номер артикула
     * @param driver - об'єкт вебдрайвера
     * @throws InterruptedException - повідомлення про помилку
     */
    public void getSearchArticle(String searchArticulNum, WebDriver driver) throws InterruptedException {
        try {
            WebElement dynamicElement = (new WebDriverWait(driver, 10))
                    .until(ExpectedConditions.presenceOfElementLocated(By.className("article-cell")));
            if(dynamicElement == null){
                throw new RuntimeException("не можливо отримати об'єкт веб елемента");
            }
        }catch (Exception e){
            throw new RuntimeException("Не можливо здiйснити очікування елемента: поле артикула");
        }

        waitForElementByXpath("//div/div/div[3]", 20 , driver);
        clickSelectorByXpath(driver,"//div/div/div[3]","Не можливо відкрити ПОП-АП");


        String articuleFieldTextVal;
        waitForElementByXpath("//div/input", 20 , driver);
        try{
            WebElement articuleField = driver.findElement(By.xpath("//div/input"));
            articuleFieldTextVal = articuleField.getText();
        } catch (Exception e){
            throw new RuntimeException("не можливо отримати текстове значення поля артикул");
        }
        if(!articuleFieldTextVal.equalsIgnoreCase("")){
            try {
                driver.findElement(By.xpath("//div/input")).clear();
            } catch (Exception e){
                throw new RuntimeException("не можливо очистити поле Артикул в POP UP меню");
            }
        }

        Thread.sleep(3500);
        sendKeysByXpath(driver,"//div/input","Не можливо ввести текст в поле артикул",searchArticulNum);

        clickSelectorByXpath(driver,"//button[2]","Не можливо натиснути кнопку (відібрати) POP UP меню");

        waitForElementByXpath("//form/div[6]/button[2]", 15 ,driver);

        isElementDisabledAlertByXpath("//form/div[6]/button[2]",driver);

        clickSelectorByXpath(driver,"//div[6]/button[2]", "Не можливо натиснути кнопку (OK) POP UP меню");

        waitForElementByXpath("//section[1]/div/div[1]/div", 15, driver);
    }

    /**Метод, що здійснює пошук значення місяця для для коректного його пошуку в календарі початку/закінчення періоду
     * @param date - значення місяця
     * @return - коректне значення місяця
     */
    public int returnDate (int date){
        return (date <3) ? (date + 9) : (date + 1);
    }

    /**Відкриття вибраного робочого столу з інформацією по вказаному артикулу
     *  @param driver driver - об'єкт вебдрайвера
     * @param desktopXpath - шлях до робочого столу
     * @param searchProvider - назва постачальника
     * @param desctopName - Текстове ім'я робочого столу (для повідомлень)
     * @throws Exception
     */
    public void openDesktopWithArticleByXpath(WebDriver driver, String desktopXpath,
                                              String searchProvider, String desctopName, String baseUrl,
                                              String userLogin, String userPassword, String currentDomain) throws Exception {
        openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
        searchProvider(searchProvider,driver);
        waitForElementByXpath("//div[3]/select", 15 ,driver);

        clickSelectorByXpath(driver, "//button[2]",
                "Не можливо натиснути кнопку (Відібрати) POP UP Меню");

        waitForElementByXpath("html/body/div[3]/div/form/div[4]/div/ul/li/span[2]",15,  driver);

        clickSelectorByXpath(driver,"//div[6]/button[2]",
                "Не можливо натиснути кнопку ОК POP UP Menu");

        String desctopVal = getFieldTextByXpath(driver,
                desktopXpath);
        if(desctopVal.equalsIgnoreCase("Даних немає")){
            throw new RuntimeException(String.format("В інфоблоці %s - даних не знайдено",desctopName));
        }
    }

    /**Метод для додавання задічі (бага) до TFS з використанням TFS-REST_API
     * Для того,щоб задачка додавалась до ТФС потрібно мати:
     * - чітку назву проекту
     * - Пароль та логін валідного користувача
     * - знати домен
     * @param steckTrace - повний трейс з  текстом помилки, що передається в тілі запиту (буде відображатись в кроках)
     * @param errorMessage - текст помилки, що спрацював при ексепшені (Українською мовою, вказані користувачем)
     * @param testName - Текстова назва тесту, що вказана в анотації
     * @throws IOException - помилка
    */
    public void OkHTTP_request_TFS_ADD_task(String steckTrace, String errorMessage, String testName)throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().
                authenticator(new NTLMAuthenticator("i.pedorych", "Bombini2392", "OFFICEUKRAINE")).build();
        MediaType mediaType = MediaType.parse("application/json-patch+json");
        JsonArray jsonBody = Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                        .add("op", "add")
                        .add("path", "/fields/System.Title")
                        .add("value", String.format("[%s] %s", testName, errorMessage))
                )
                .add(Json.createObjectBuilder()
                        .add("op", "add")
                        .add("path", "/fields/Microsoft.VSTS.TCM.ReproSteps")
                        .add("value", "<b style=\"color:red\">Трейс помилки:</b> " +
                                "<br><br><hr><p style=\"color:blue\">" + steckTrace.replace("\r\n","<br>"))
                )
                .build();
        StringWriter bodyBuilder = new StringWriter();
        JsonWriter jsonWriter = Json.createWriter(bodyBuilder);
        jsonWriter.writeArray(jsonBody);
        jsonWriter.close();

        //RequestBody body = RequestBody.create(mediaType, "[\r\n    {\r\n        \"op\": \"add\",\r\n        \"path\": \"/fields/System.Title\",\r\n        \"value\": \"Помилка при перевірці автотестом: \"" + testName + "\"\"\r\n    },\r\n    {\r\n        \"op\": \"add\",\r\n        \"path\": \"/fields/Microsoft.VSTS.TCM.ReproSteps\",\r\n        \"value\": \\\"Текст помилки: \n\"" + errorMessage + "\"\"\r\n    }\r\n\r\n\r\n]\r\n");
        RequestBody body = RequestBody.create(mediaType, bodyBuilder.toString());
        Request request = new Request.Builder()
                .url("http://tfs2013:8080/tfs/DefaultCollection/SalesForecast/_apis/wit/workitems/$Bug?api-version=1.0")
                .patch(body)
                .addHeader("content-type", "application/json-patch+json")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "9fee055c-a5b0-795e-7f5c-4b68ddefc402")
                .build();
        Response response = client.newCall(request).execute();
        if(response == null){
            throw new RuntimeException("не можливо отримати об'єкт веб елемента");
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", credentials).build();
        return chain.proceed(authenticatedRequest);
    }

    //------------------------CatchErrors---------------------------------

    /**Здійснення кліку на елемент по xPath
     * @param driver  - об'єкт вебдрайвера
     * @param xPath - шлях до елемента
     * @param errorMessage - повідомлення в разі коли не вдається клікнути на елемент
     */
    public void clickSelectorByXpath(WebDriver driver, String xPath, String errorMessage){
        try {
            driver.findElement(By.xpath(xPath)).click();
        } catch (Exception e){
            throw new RuntimeException(errorMessage) ;
        }
    }

    /** Клік на елемент по назві класу
     * @param driver об'єкт вебдрайвера
     * @param classPath шлях до об'єкта
     * @param errorMessage тексто повідомлення (загтовка, коли не можливо здійснити клік)
     */
    public void clickSelectorByClass(WebDriver driver, String classPath, String errorMessage){
        try {
            driver.findElement(By.className(classPath)).click();
        } catch (Exception e){
            throw new RuntimeException(errorMessage) ;
        }
    }

    /** Клік на елемент по назві класу
     * @param driver об'єкт вебдрайвера
     * @param xPath шлях до об'єкта
     * @param errorMessage тексто повідомлення (загтовка, коли не можливо здійснити клік)
     */
    public void sendKeysByXpath(WebDriver driver, String xPath, String errorMessage, String sendKeysVal) throws InterruptedException {
        Thread.sleep(3000);
        try {
            driver.findElement(By.xpath(xPath)).sendKeys(sendKeysVal);
        } catch (Exception e){
            throw new RuntimeException(errorMessage) ;
        }
    }

    /** Метод для визначення стану робочих столів (вимкнений/увімкнений)
     *  @param driver об'єкт вебдрайвера
     * @param deskTopAttribute атрибут по якому здійснюється пошук робочого столу,
     * масив з атрибутами: deskTopAttributeBase
     */
    public void checkWorkDesktopAttribute(WebDriver driver, String deskTopAttribute){
        Boolean deskTopEnabled = false;
        String [] deskTopAttributeBase = {"block-forecast","block-shares", "block-charts", "block-reports"};
//        String [] deskTopAttribute = {"block-forecast","block-shares", "block-charts", "block-reports"};
        String workDesktop1AtrVal = "";
        String workDesktop2AtrVal = "";
        WebElement workDesktop1;
        WebElement workDesktop2;

        for (int i = 0; i < 4; i++){
            try {
                workDesktop1 = driver.findElement(By.xpath("//section[2]/div/div[1]"));
            } catch (Exception e){
                throw new RuntimeException("Не можливо отримати атрибут перешого робочого столу");
            }

            workDesktop1AtrVal= workDesktop1.getAttribute(deskTopAttributeBase[i]);
            if(workDesktop1AtrVal == null){
                workDesktop1AtrVal = "";
            }
            if(workDesktop1AtrVal.equalsIgnoreCase(deskTopAttribute)){
                deskTopEnabled = true;
                break;
            }

            try {
                workDesktop2 = driver.findElement(By.xpath("//section[2]/div/div[2]"));
            } catch (Exception e){
                throw new RuntimeException("Не можливо отримати атрибут другого робочого столу");
            }

            workDesktop2AtrVal = workDesktop2.getAttribute(deskTopAttributeBase[i]);
            if(workDesktop2AtrVal == null){
                workDesktop2AtrVal = "";
            }
            if(workDesktop2AtrVal.equalsIgnoreCase(deskTopAttribute)){
                deskTopEnabled = true;
                break;
            }

            if(i == 3 && !deskTopEnabled == false){
             throw new RuntimeException(String.format("Не можливо знайти Робочий стіл %s", deskTopAttribute));
            }
        }
    }

    /** Метод для звірки значення прогнозу з інфоблоку Прогноз з робочим столом - Прогноз
     * даний метод звіряє значення прогнозу лише для одиничного артикула
     *  @param driver об'єкт вебдрайвера
     */
    public void verifyForecastDesktop(WebDriver driver) {
        WebElement forecastEl;
        WebElement deskTopEl;

        try{
            forecastEl = driver.findElement(By.xpath("//section[1]/div/div[1]/div/div"));
        } catch (Exception e) {
            throw new RuntimeException("Не можливо отримати величину прогнозу з інфоблоку");
        }

        try {
            deskTopEl = driver.findElement(By.xpath("//tr/td[2]"));
        } catch (Exception e) {
            throw new RuntimeException("Не можливо отримати величину прогнозу з робочого столу");
        }

        if(!forecastEl.getText().equals(deskTopEl.getText())){
            throw new RuntimeException("Прогноз інфоблоку не відповідає прогнозу робочого столу");
        }
    }

    /** метод для звірки значення інфоблоку прогноз зі сумою значень робочого столу прогноз
     * даний метод додає усі значення прогнозів в робочому столі
     * @param driver об'єкт вебдрайвера
     * @throws InterruptedException помилка в разі, якщо значення прогнозів не співпадають
     */
    public void checkSumForecastDeskTop (WebDriver driver) throws InterruptedException {
        int sumOfColumnDeskTop;
        int infoblockForecastVal;

        waitForElementByXpath("//section[2]/div/div/div/span/i", 15,  driver);
        try{
            Collection<WebElement> column = driver.findElements(By.
                    cssSelector("section.desktop.ng-scope table.table-condensed > tbody > tr > td.col-center.ng-binding.ng-scope"));
            sumOfColumnDeskTop = column.stream().mapToInt(e -> Integer.parseInt(e.getText().replace(" ", ""))).sum();
        } catch (Exception e){
            throw new RuntimeException("Не можливо отримати суму прогнозів з робочого столу");
        }

        try{
            infoblockForecastVal = Integer.parseInt(driver.findElement(By.
                    xpath("//section[1]/div/div[1]/div/div")).getText().replace(" ", ""));
        }catch (Exception e) {
            throw new RuntimeException("Не можливо отримати значення прогнозу з інфоблоку");
        }

        if(sumOfColumnDeskTop != infoblockForecastVal){
            throw new RuntimeException("Відрізняється значення прогнозу в інфоблоці та робочому столі");
        }

    }

    /** Метод для перевірки стану чекбоксів
     * даний метод видає помилку в разі, коли в чекбоксі не стоїть галочка
     * Використовується для графіків, може використовуватись для будь-яких інших елементів
     * @param driver об'єкт вебдрайвера
     * @param xPath шлях до елеменета чекбокса
     */
    public void checkBoxDisabledAlertByXpath(WebDriver driver, String xPath) throws InterruptedException {
        Boolean state;
        waitForElementByXpath(xPath, 10, driver);
        try {
            WebElement checkBoxSales = driver.findElement(By.xpath(xPath));
            state = checkBoxSales.isSelected();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Не можливо отримати стан чекбокса %s", xPath));
        }
        if(!state){
            throw new RuntimeException(String.format("Не проставлена галочка чекбокса %s", xPath));
        }
    }

    /** Метод для перевірки стану чекбоксів
     * даний метод видає помилку в разі, коли в чекбоксі стоїть галочка
     * Використовується для графіків, може використовуватись для будь-яких інших елементів
     * @param driver об'єкт вебдрайвера
     * @param xPath шлях до елеменета чекбокса
     */
    void checkBoxEnabledAlertByXpath(WebDriver driver, String xPath) {
        Boolean state;

        try {
            WebElement checkBoxSales = driver.findElement(By.xpath(xPath));
            state = checkBoxSales.isSelected();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Не можливо отримати стан чекбокса %s", xPath));
        }
        if(state){
            throw new RuntimeException(String.format("Проставлена галочка чекбокса %s", xPath));
        }
    }

    /** Метод отримує код відповіді сторінки/сайту. Метод перевіряє стан підключення до ресурсу.
     * метод видає помилку в разі, коли повертаються помилки в результаті виконання запиту
     * @throws IOException помилка спрацьовує на усі коди -  >= 400
     */
    public void catchStatusLoadPage() throws IOException {
        URL url = new URL("http://crosstest.fozzy.lan/SalesForecast/");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        try {
            connection.connect();
        }catch (UnknownHostException e){
            throw new RuntimeException("Не можливо отримати IP адресу сервера");
        } catch (SocketTimeoutException e){
            throw new RuntimeException("Не можливо пiдключитися до сервера");
        }

        int requestCode = connection.getResponseCode();
        if(requestCode >= 400){
            throw new RuntimeException("Не модливо завантажити сторінку! Код помилки: " + requestCode) ;
        }
    }

    /**Метод для автоматичного вимкнення графіків (потенційні продажі, прогноз)
     * працює лише при відкритому робочому столі графіки і в разі, коли дані графіки вибрані по замовчуванню
     * @param driver об'єкт вебдрайвера
     * @throws InterruptedException помилка при неможливості вимкнути графіки
     */
    public void getOffDefaultGraphics(WebDriver driver) throws InterruptedException {
        waitForElementByXpath("//div[1]/div/div/div[2]/div[1]/input", 20, driver);

        Thread.sleep(3500);
        clickSelectorByXpath(driver,"//div[2]/label",
                "Не можливо натиснути на чекбокс потенційні продажі");

        clickSelectorByXpath(driver,"//div[1]/div[4]/label",
                "Не можливо натиснути на чекбокс Прогноз");

        String getText;
        String alertMessage = "Невибрано графік з даними !";
        try {
            WebElement text = driver.findElement(By.cssSelector("#chart-forecast > svg > g:nth-child(2) > text"));
            getText  = text.getText();
        } catch (Exception e){
            throw new RuntimeException("Не можливо отримати текст повідомлення з графіків");
        }

        if(!alertMessage.equalsIgnoreCase(getText)){
            throw new RuntimeException("Відсутнє повідомлення про відсутність вибору графіків");
        }
    }

    /**Метод для автоматичного відкриття робочого столу Звіти
     * використовується лише після того, як було знайдено хоча б один артикул
     * @param driver об'єкт вебдрайвера
     * @throws InterruptedException помилка відкриття
     */
    public void openReportDeskTop(WebDriver driver) throws InterruptedException {
        Thread.sleep(1500);
        waitForElementByXpath("//section[1]/div/div[6]/div", 25,  driver);
        clickSelectorByXpath(driver, "//section[1]/div/div[6]/div",
                "не можливо натиснути кнопку інфоблок робочий стіл Звіти");

        waitForElementByXpath("//*[@id=\"single-button\"]", 25, driver);
        isElementDisabledAlertByXpath("//*[@id=\"single-button\"]",driver);
    }

    /** Метод для перевірки наявності імені класу (універсальний метод)
     *  @param element бебелемент, який перевіряється на наявність імені класу
     * @param htmlClass ім'я класу, що шукається
     * @return повертає true в разі знаходження імені класу та false якщо ім'я не знайдене
     */
    public boolean hasClass(WebElement element, String htmlClass) {
        String[] cl = element.getAttribute("class").split("\\s+");
        if (cl != null) {
            for (String classAttr: cl) {
                if (classAttr.equals(htmlClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Метод перевіряє чи створився файл по вказаному шляху
     *  @param fileName ім'я фалу, що який перевіряється
     */
    public void isFileExist(String fileName, Path downloadFilepath){
        Boolean isExist;

        try {
            isExist = downloadFilepath.resolve(fileName).toFile().isFile(); // reports (1).xls
        }catch (Exception e) {
            throw new RuntimeException("Не можливо перевірити створення файлу");
        }
        if(!isExist){
            throw new RuntimeException(String.format("Не знайдено файлу з назвою: %s", fileName));
        }
    }

    /** переключення до аналітичного звіту
     * метод працює лише, з відкритим робочим столом - Звіти
     * @param driver об'єкт вебдрайвера
     * @throws InterruptedException помилка переключення до аналітичного звіту
     */
    public void switchToAnalyticReport (WebDriver driver) throws InterruptedException {

            waitForElementByXpath("//div[2]/select", 20, driver);
            try {
                Select dropdown = new Select(driver.findElement(By.xpath("//div[2]/select")));
                dropdown.selectByIndex(1);
            }catch (Exception e){
                throw new RuntimeException("Не можливо аибрати Аналітичний звіт у Випадаючому списку");
            }

            waitForElementByXpath("//*[@id=\"mCSB_1_container\"]/table[3]/thead/tr[2]/th[5]", 20,
                    driver);

            WebElement columnName = driver.
                    findElement(By.xpath("//*[@id=\"mCSB_1_container\"]/table[3]/thead/tr[2]/th[5]"));

            if(!columnName.getText().equalsIgnoreCase("Група XYZ")){
                throw new RuntimeException("Не відбулось переключення до Аналітичного звіту");
            }
    }

    /** Перевірка на існування елемента по його xPath
     * @param xPath шлях до елемента
     * @param elementName ім'я для елемента - використовується при виведенні помилки
     * @param driver об'єкт вебдрайвера
     * @return повертає булієвське значення true якщо елемент знаходиться і false, якщо не знаходиться
     */
    public boolean ifElementExist(String xPath, String elementName, WebDriver driver){
        boolean exist;

        try {
            exist = driver.findElements( By.xpath(xPath) ).size() != 0;
        }catch (Exception e){
            throw new RuntimeException(String.format("Не можливо перевірити наявність елемнта: %s", elementName));
        }

        return exist;
    }

    /**Метод отримує список елементів таблиці і проводить їх сортування в прямому порядку
     * даний метод використовується для сортування інтових значень артикулів
     * @param cssSel шлях до таблиці
     * @param driver об'єкт вебдрайвера
     * @return повертає список отриманих і відсортованих інтових значень аритикулів
     */
    public List<Integer> checkSortArticulesByCss(String cssSel, WebDriver driver) {
        List<Integer> obtainedList;

        List<WebElement> tableEl;
        try {
            tableEl = driver.findElements(By.cssSelector(cssSel));
        } catch (Exception e){
            throw new RuntimeException("Не можливо отримати елементи таблиці артикулів");
        }
        obtainedList = tableEl.stream().map(e -> Integer.parseInt(e.getText())).collect(Collectors.toList());
        obtainedList.sort(Integer::compareTo);

        return obtainedList;
    }

    /**Метод отримує список елементів таблиці і проводить їх сортування в оберненому порядку
     * даний метод використовується для сортування інтових значень артикулів
     * @param cssSel шлях до таблиці
     * @param driver об'єкт вебдрайвера
     * @return повертає список отриманих і відсортованих інтових значень аритикулів
     */
    public List<Integer> checkReveceSortArticulesByCss(String cssSel, WebDriver driver) {
        List<Integer> reverseSort = checkSortArticulesByCss(cssSel, driver);

        reverseSort.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                Integer minus_o1 = -o1;
                return minus_o1.compareTo(-o2);
            }
        });
        return reverseSort;
    }

    /**Метод для здійснення логінізації (в методі присутня перевірка на перебування на сторінці логінізації)
     * @param userLogin логін користувача що вводиться в поле логін
     * @param userPassword пароль користувача що вводиться в поле пароль
     * @param domain вибір домена з випадаючого списку по його назві
     * @param driver об'єкт вебдрайвера
     * @throws IOException спрацювання на вийняткові події
     * @throws InterruptedException спрацювання на вийняткові події
     */
    public void loginUser(String userLogin, String userPassword, String domain, WebDriver driver) throws IOException, InterruptedException {
        catchStatusLoadPage();

        String currentUrlVal;
        try {
            currentUrlVal = driver.getCurrentUrl();
        } catch (Exception e){
            throw new RuntimeException("Не можливо отримати поточного значення URL");
        }

        if(currentUrlVal.equals("http://crosstestxxxxxxxxxxx/SalesForecast/#/login")){
            waitForElementByXpath("//form/input[1]", 10, driver);
            try {
                Select dropdown;
                dropdown = new Select(driver.findElement(By.xpath("//form/select")));
                dropdown.selectByVisibleText(domain);
            }catch (Exception e){
                throw new RuntimeException("Не можливо вибрати DOMAIN OFFICEUKRAINE");
            }
            inputValue(driver, "//form/input[1]", userLogin);
            inputValue(driver, "//form/input[2]", userPassword);

            clickSelectorByXpath(driver, "//form/button", "Не можливо здійснити клік на кнопку Вход при логінізації");

            WebElement dynamicElement = (new WebDriverWait(driver, 15))
                    .until(ExpectedConditions.presenceOfElementLocated(By.className("article-cell")));
            if(dynamicElement == null){
                throw new RuntimeException("Логінізація пройшла з помилкою");
            }
        }

    }

    /**Метод для тестової авторизації користувача (не відбувається перевірка поточного урл)
     * @param userLogin логін користувача що вводиться в поле логін
     * @param userPassword пароль користувача що вводиться в поле пароль
     * @param domain вибір домена з випадаючого списку по його назві
     * @param driver об'єкт вебдрайвера
     * @throws IOException спрацювання на вийняткові події
     * @throws InterruptedException спрацювання на вийняткові події
     */
    public void testLoginUser(String userLogin, String userPassword, String domain, WebDriver driver) throws IOException, InterruptedException {
        catchStatusLoadPage();

        try {
            Select dropdown;
            dropdown = new Select(driver.findElement(By.xpath("//form/select")));
            dropdown.selectByVisibleText(domain);
        }catch (Exception e){
            throw new RuntimeException(String.format("Не можливо вибрати DOMAIN: %s", domain));
        }
        inputValue(driver, "//form/input[1]", userLogin);
        inputValue(driver, "//form/input[2]", userPassword);

        clickSelectorByXpath(driver, "//form/button", "Не можливо здійснити клік на кнопку Вход при логінізації");

    }

    /**Метод перевірки об'єкта веб елеемента на наявність атрибута Required
     * @param driver об'єкт вебдрайвера
     * @param fieldXPath шлях до елемента
     * @throws RuntimeException Відображення помилок в разі неможливості виконати операцію
     */
    public void checkRequiredField (WebDriver driver, String fieldXPath) throws RuntimeException {
        WebElement inputField;
        try{
            inputField = driver.findElement(By.xpath(fieldXPath));
        } catch (Exception e){
            throw new RuntimeException("Не можливо отримати вебелемент поля вводу логіна або пароля");
        }
        String inputFieldText = inputField.getAttribute("class");


        if(inputFieldText.contains("ng-valid"))
            throw new RuntimeException("Не коректно працює перевірка на пустоту поля логін або пароль");

    }




}
