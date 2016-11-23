import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.annotations.Test;
import ru.stqa.selenium.factory.WebDriverFactory;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import static junit.framework.Assert.assertEquals;

/**
 *Основний клас для написання тест-сьютів та тесткейсів
 */
public class FozzySalesForecastTest implements IRetryAnalyzer {
    private static WebDriver driver;
    private final  String userLogin = "xxxxx";
    private final  String userPassword = "xxxxx";
    private final  String currentDomain = "xxxxx";

    private File myTempDir = Files.createTempDir();

    /**
     * Фіксований шлях для збереження звітів
     */
    private final Path downloadFilepath = myTempDir.toPath();//".\\myTempDir";


    private final String baseUrl = "http://crosstestxxxxxxxx/SalesForecast/";
    /**
     * Масиви з xPath для календарів (початок, кінець періодів),
     * а також масив з назвами відповідних місяців
     */
    private final String[] monthXpath = {
            "//tr[1]/td[1]/button",
            "//tr[1]/td[2]/button",
            "//tr[1]/td[3]/button",
            "//tr[2]/td[1]/button",
            "//tr[2]/td[2]/button",
            "//tr[2]/td[3]/button",
            "//tr[3]/td[1]/button",
            "//tr[3]/td[2]/button",
            "//tr[3]/td[3]/button",
            "//tr[4]/td[1]/button",
            "//tr[4]/td[2]/button",
            "//tr[4]/td[3]/button",
            "//th[2]/button"};
    private final String[] monthName = {
            "січень",
            "лютий",
            "березень",
            "квітень",
            "травень",
            "червень",
            "липень",
            "серпень",
            "вересень",
            "жовтень",
            "листопад",
            "грудень"};


    /**
     * Створення об'єкта з окремо винесеними функціями
     */
    private Utilities getFunc = new Utilities();

    //Запуск об'єкта хром-драйвера

    /**
     * Конфігурування шляхів та об'єкта вебдрайвера
     * Запуск вебдрайвера з відповідними конфігураціями
     * Використовується WebDriverFactory - гібридна фабрика, що контролює один екземпляр хром драйвера
     */
    private void startBrowser() {
        String workDirectory = System.getProperty("user.dir");
        Path driverPath = Paths.get(workDirectory).resolve("chromedriver.exe");
        //boolean driverExists = driverPath.toFile().exists();
        System.setProperty("webdriver.chrome.driver", driverPath.toString());

        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", downloadFilepath.toString());
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", chromePrefs);
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);

        driver = WebDriverFactory.getDriver(capabilities);


        driver.manage().window().maximize();
    }


    //Здійснення повтору тестів. масимальна к-сть спроб - 3

    /**
     * Оголошення змінних для лічильника повторень тестувань та отримання величини поточного часу
     */
    private int retryCount = 0;
    private int maxRetryCount = 2;

    /**
     * Функція для здійснення повторного запуску тестів
     * дана функція використовує метод
     * @param result - результат проходження тесту OkHTTP_request_TFS_ADD_task - для додавання задач (багів в TFS)
     * @return - повертає true/false у випадку проходження або краху тесту
     */
    public boolean retry(ITestResult result) {
        if(result != null && result.isSuccess()){ return false; }
        if (retryCount < maxRetryCount) {
            retryCount++;
            return true;
        }

        /**
         * Додавання помилки до TFS
         */
        Throwable exception = result.getThrowable();
        if (exception != null) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(os);
            exception.printStackTrace(ps);
            /**Текст трейсу помилки*/
            String stackTrace = new String(os.toByteArray(), StandardCharsets.UTF_8);
            /**Ім'я анотації*/
            String annotationName = result.getMethod().getMethod().getDeclaredAnnotation(Test.class).testName();
            /**Ім'я анотації*/
            String testName = annotationName != null ? annotationName : result.getMethod().getMethodName();
            String alertMessage = exception.getMessage();

            try {
                getFunc.OkHTTP_request_TFS_ADD_task(stackTrace, alertMessage, testName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Функція для припинення роботи об'єкта хром драйвера
     */
        private void stopBrowser() {
        WebDriverFactory.dismissDriver(driver);
    }

    //-------------------------------------------------------------------------------------------------------//
    //------------------------------------------Тести--------------------------------------------------------//

    /**
     * Тест: Перевірка відкриття PopUp вікна
     * @throws Exception - В разі неможливості доступитись до будь-якого з елементів
     */
    @Test(testName = "Перевірка відкриття PopUp вікна", groups = "filterPanel",
            retryAnalyzer = FozzySalesForecastTest.class)
    public void A_openPopUpWindowTest_1() throws Exception {
        startBrowser();
        getFunc.catchStatusLoadPage();
        driver.get(baseUrl);
        getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
        System.gc();
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Перевірка елементів плей-ліста",groups = {"filterPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void B_playListTest_2() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);

        /**Штучні паузи, значення вказується в мілісекундах */
        Thread.sleep(1500);

        getFunc.clickSelectorByClass(driver,"button-default",
                "Не можливо здійснити клік на кнопку (...)");

        Thread.sleep(2500);
        getFunc.clickSelectorByXpath(driver,"//textarea",
                "Не можливо здійснити клік на текстове поле мультівставки");

        getFunc.sendKeysByXpath(driver,"//textarea",
                "Не можливо надрукувати текст в форму Мультівставка",
                "125036\n125037\n125038\n125039\n125040\n125041\n125042\n125043\n125044\n125045\n125046\n125047" +
                        "\n125048\n125049\n125050\n125051\n125052\n125053\n125054\n125055\n125056\n125057\n125058\n125059" +
                        "\n");

        getFunc.clickSelectorByClass(driver,"button-basic",
                "Не можливо натиснути кнопку ОК форми мультівставки");

        Thread.sleep(1500);
        getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                "Не можливо натиснути кнопку Відібрати POP UP Меню");

        Thread.sleep(2500);
        getFunc.clickSelectorByClass(driver,"fa-chevron-right",
                "Не можливо натиснути кнопку переключення на наступну сторінку");

        Thread.sleep(1500);
        getFunc.clickSelectorByClass(driver,"fa-chevron-left",
                "Не можливо натиснути кнопку переключення на попередню сторінку");

        Thread.sleep(1500);
        getFunc.clickSelectorByClass(driver,"fa-angle-double-right",
                "Не можливо натиснути кнопку переключення на останню сторінку");

        Thread.sleep(1500);
        getFunc.clickSelectorByClass(driver,"fa-angle-double-left",
                "Не можливо натиснути кнопку переключення на першу сторінку");

        Thread.sleep(1500);
        getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                "Не можливо нажати кнопку ОК плей ліста");

        Thread.sleep(1500);
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Перевірка елемента артикула головного меню",groups = {"filterPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void C_mainMenuElemArtTest_3() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
        Thread.sleep(1500);

        //getFunc.clickSelectorByXpath(driver, "//input", "Не можливо");
        getFunc.sendKeysByXpath(driver,"//input",
                "Не можливо ввести артикул в поле артикула","136");

        getFunc.clickSelectorByXpath(driver, "//button[2]",
                "Не можливо натиснути кнопку (Відібрати) POP UP Меню");

        Thread.sleep(3500);
        getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                "Не можливо нажати кнопку ОК плей ліста");

        try {
            assertEquals(driver.findElement(By.cssSelector("p.name_product.ng-binding"))
                    .getText(), "Зарод250КингСтевия");
        } catch (Error e) {
            throw new RuntimeException(String.format("Не знайдено артикула: %s, на панелі фільтрів",
                    "Зарод250КингСтевия"));
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Пошук артикулів по імені",groups = {"filterPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void D_elementProductNameTest_4() throws Exception {
        startBrowser();
        getFunc.catchStatusLoadPage();

        /**
         * Подібним чином описуються масиви з даними для введення у вказані поля
         */
        String[] inputValues = {"1235", "палка", "Сушка Київхліб", "ёёё", "ъъъ", "00000000000000000"};
        String[] compareValues = {"1235", "палка", "Сушка Київхліб", "Даних не знайдено !", "Даних не знайдено !",
                "Даних не знайдено !"};

        /**
         * open pop-up window
         */
        getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
        /**
         * search product by name (part of name) and clear pop up window
         */

        for(int i = 0; i < inputValues.length; i++){
            switch (i){

                case 0:
                case 1: {
                    getFunc.searchProductList(inputValues[i], driver);
                    getFunc.GetText(".list-provider-name.ng-binding", compareValues[i], driver);
                    getFunc.checkPlayListPagesForValues(driver, compareValues[i]);
                    getFunc.clearPopUp(driver);
                    break;
                }
                case 2:
                case 3:
                case 4:
                case 5:{
                    getFunc.searchProductList(inputValues[i], driver);
                    getFunc.GetText(".list-provider-name.ng-binding", compareValues[i], driver);
                    getFunc.clearPopUp(driver);
                    break;
                }
                default:{
                    throw new RuntimeException("Не вірно визначена довжина масиву даних, що вводяться!");
                }
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Перевірка кнопки 'Відібрати'", groups = {"filterPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void E_checkSelectButtonConditionTest_5() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        /**
         * Масив даних, що вводяться в поля
         */
        String[] inputValues = {" ", "___", "***", "*?*", "/*-+Ж/\\^[a-z0-9_-]{3,1'5}$\""};

        getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);

        //Check button "Відібрати" for condition
        for (String inputValue : inputValues) {
            getFunc.inputValue(driver, "//form/div[1]/div[1]/input", inputValue);
            Thread.sleep(1500);
            if (driver.findElement(By.xpath("//button[2]")).isEnabled()) {
                throw new RuntimeException("Кнопкуа\"Відібрати\" активна");
            }
            getFunc.clearPopUp(driver);
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Перевірка пошуку одиничного артикула",groups = {"filterPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void F_inputSingleArticleTest_6() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        String[] inputValues = {"1", "0", "", "00000001", "текст", "0000099", "/*-+=\\)([]_", "100235", "999999999999999"};
        String[] expectedResult = {"Горчица Верес с хреном", "Даних не знайдено !",
                "", "Горчица Верес с хреном", "", "Минеральная вода Калипсо сильногазиров.",
                "", "Чай Детский Фенхельевый", "Даних не знайдено !"};

        getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);

        for (int i = 0; i < inputValues.length; i++) {
            getFunc.inputValue(driver, "//input", inputValues[i]);
            if (driver.findElement(By.xpath("//button[2]")).isEnabled() && (i == 2 || i == 4 || i == 6)) {
                throw new RuntimeException("Кнопкуа\"Відібрати\" активна");
            }
            getFunc.clickSelectorByXpath(driver,"//button[2]",
                    "Не можливо натиснути кнопку \"Відібрати\"");

            Thread.sleep(1500);
            getFunc.GetText(".list-provider-name.ng-binding", expectedResult[i], driver);
            getFunc.clearPopUp(driver);
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Перевірка відбору мультівставки артикулів",groups = {"filterPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void G_multiInputArticlesTest_7() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        String[] inputValues = {"125036\n" + "125037\n" + "125038\n" + "125039\n" + "125040\n" + "125041\n" + "125042\n" +
                "125043\n" + "125044\n" + "125053\n",

                "125054\n" + "/*-+Ж/\\^[a-z0-9_-]{3,1'5}$\"\n" + "125072\n" + "1,00E+73\n" + "***\n" + "125075\n" +
                        "їїїї\n" + "125077\n" + "''''\n" + "ё\n",

                "125036123123\n" + "125036123124\n" + "125036123126\n" + "125036123127\n" + "125036123128\n" +
                        "125036123129\n" + "125036123120\n" + "125036123121\n" + "125036123122\n",

                "я\r\n" + "чч\r\n" + "ссс\r\n" + "ммм\r\n" + "иииии\r\n" + "0с0с0с0с0с\r\n" + "і4і4і4і4і4і4і\r\n" + "й6й6й6й6й\r\n" +
                        "ї7ї7ї7ї7ї7ї7ї7ї\r\n",

                "125036\n" + "125037\n" + "125038\n" + "125039\n" + "125040\n" + "125041\n" + "125042\n" + "125043\n" +
                        "125044\n" + "125045\n" + "125046\n" + "125047\n" + "125048\n" + "125049\n" + "125050\n" + "125051\n" +
                        "125052\n" + "125053\n" + "125054\n" + "125055\n" + "125056\n" + "125057\n" + "125058\n" + "125059\n" +
                        "125060\n" + "125061\n" + "125062\n" + "125063\n" + "125064\n",

                "1\n" + "22\n" + "333\n" + "4444\n" + "55555\n" + "666666\n" + "7777777\n" + "88888888\n" + "999999999\n" +
                        "0,0000000001\n" + "0\n",

                "125041\n" + "125072\n" + "10073\n" + "12505409315\n" + "125075\n" + "125077",

                "125041\n" + "125072\n" + "10073\n" + "125075\n" + "125077" + "12505409315\n",

                "12505409315\n" + "125041\n" + "125072\n" + "10073\n" + "125075\n" + "125077",

                "125036\n" + "125037\n" + "125038\n" + "125039\n" + "125040\n" + "125041\n" +
                        "125042\n" + "125043\n" + "125044\n" + "125045\n" + "125046\n" + "125047\n" +
                        "125048\n" + "125049\n" + "125050\n" + "125051\n" + "125052\n" + "125053\n" +
                        "125054\n" + "125055\n" + "125056\n" + "125057\n" + "125058\n" + "125059\n" +
                        "125060\n" + "125061\n" + "125062\n" + "125063\n" + "125064\n" + "37834\n" +
                        "37835\n" + "37836\n" + "37837\n" + "37838\n" + "37839\n" + "37840\n" + "37841\n" +
                        "37842\n" + "37843\n" + "37844\n" + "37845\n" + "37846\n" + "37847\n" + "37848\n" +
                        "37849\n" + "37850\n" + "37851\n" + "37852\n" + "37853\n" + "37854\n" + "37855\n" +
                        "37856\n" + "37857\n" + "37858\n" + "37859\n" + "37860\n" + "37861\n" + "37862\n" +
                        "37863\n" + "37864\n" + "37865\n" + "37866\n" + "37867\n" + "37868\n" + "37869\n" +
                        "37870\n" + "37871\n" + "37872\n" + "37873\n" + "37874\n" + "37875\n" + "37876\n" +
                        "37877\n" + "37878\n" + "37879\n" + "37880\n" + "37881\n" + "37882\n" + "37883\n" +
                        "37884\n" + "37885\n" + "37886\n" + "37887\n" + "37888\n" + "37889\n" + "37890\n" +
                        "37891\n" + "37892\n" + "37893\n" + "37894\n" + "37895\n" + "37896\n" + "37897\n" +
                        "37898\n" + "37899\n" + "37900\n" + "37901\n" + "37902\n" + "37903\n" + "37904\n" +
                        "37905\n"
        };


        getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
        for (int i = 0; i < inputValues.length; i++) {
            getFunc.inputMultiArticles(driver, inputValues[i]);
            if (!driver.findElement(By.xpath("html/body/div[3]/div/form/div[3]/button[2]")).isEnabled()) {
                throw new RuntimeException(" === The button \"Відібрати\" is NOT ACTIVE !!! === ");
            }
            Thread.sleep(1000);
            getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                    "Не можливо натиснути кнопку Відібрати POP UP Меню");
            Thread.sleep(4500);
            if (i != 2) {
                getFunc.GetTextPresentAlert(".list-provider-name.ng-binding",
                        "Даних не знайдено !", driver);
            }
            getFunc.clearPopUp(driver);
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Перевірка кнопки 'Очистити'",groups = {"filterPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void H_clearButtonTest_8() throws Exception {
        startBrowser();
        getFunc.catchStatusLoadPage();

        String[] inputValues = {"136", "тест", "тест", "Днепропак", "136", "Ком", "147809", "15, 36, 83"};

        getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);

        //input into articul
        getFunc.inputValue(driver, "//div/div/input", inputValues[0]);
        getFunc.clearPopUp(driver);
        getFunc.isFieldEmpty("vendorCode", driver);

        //input into productName
        getFunc.inputValue(driver, "//div[3]/div/form/div[1]/div[1]/input", inputValues[1]);
        getFunc.clearPopUp(driver);
        getFunc.isFieldEmpty("name", driver);

        //input  postachalnuk
        getFunc.inputValue(driver, "//div[3]/div/input", inputValues[2]);
        getFunc.clearPopUp(driver);
        getFunc.isFieldEmpty("provider", driver);

        //click "..." postachalnuk
        getFunc.inputValue(driver, "//div[3]/div/input", inputValues[3]);
        getFunc.clickSelectorByXpath(driver,"//div[3]/div/button",
                "Не можливо натиснути кнопку (...)");

        getFunc.waitForElementByName("providerList", driver);
        getFunc.clearPopUp(driver);
        getFunc.isElementEnabledAlert("providerList", driver);

        //verify result in playList
        getFunc.inputValue(driver, "//div/div/input", inputValues[4]);
        getFunc.clickSelectorByXpath(driver,"//button[2]", "Не можливо натиснути кнопку \"Відібрати\"");
        getFunc.waitForElementByXpath("html/body/div[3]/div/form/div[4]/div/ul/li/span[2]", 20, driver);
        getFunc.GetText("body > div:nth-child(3) > div > form > div.box.wrap-player > div > ul > li > span" +
                ".list-provider-name.ng-binding", "Зародыш Кинг со стевией", driver);

        getFunc.clearPopUp(driver);
        getFunc.playListCheckForClear("Зародыш Кинг со стевией", driver);

        //check all fields for clearing
        getFunc.inputValue(driver, "//form/div[1]/div[3]/div/input", inputValues[5]);
        getFunc.clickSelectorByXpath(driver,"//div[3]/div/button","Не можливо натиснути кнопку (...)");
        getFunc.waitForElementByName("providerList", driver);
        Select dropdown = new Select(driver.findElement(By.name("providerList")));
        dropdown.selectByVisibleText("Акріс ЛТД Компанія ТОВ");
        getFunc.inputValue(driver, "//div/div/input", inputValues[6]);
        getFunc.inputValue(driver, "//form/div[1]/div[1]/input", "трусы");
        getFunc.clickSelectorByXpath(driver,"//button[2]",
                "Не можливо натиснути кнопку \"Відібрати\"");

        //waitForElemenetByXpath("/html/body/div[3]/div/form/div[4]/div/ul/li/span[2]",driver);
        getFunc.GetText("body > div:nth-child(3) > div > form > div.box.wrap-player > " +
                "div > ul > li > span.list-provider-name.ng-binding", "Трусы String W22/010 SeaLine", driver);

        getFunc.waitForElementByXpath("html/body/div[3]/div/form/div[4]/div/ul/li/span[2]",20, driver);
        getFunc.clearPopUp(driver);
        getFunc.isFieldEmpty("vendorCode", driver);
        getFunc.isFieldEmpty("name", driver);
        getFunc.isFieldEmpty("provider", driver);
        getFunc.isElementEnabledAlert("providerList", driver);
        getFunc.playListCheckForClear("Трусы String W22/010 SeaLine", driver);

        //checking is playList - empty
        getFunc.inputMultiArticles(driver, "15, 36, 83");
        getFunc.clickSelectorByXpath(driver,"//button[2]",
                "Не можливо натиснути кнопку \"Відібрати\"");

        getFunc.waitForElementByXpath("html/body/div[3]/div/form/div[4]/div/ul/li/span[2]", 20, driver);
        getFunc.pageCheckForValue("Мыло SafeGuard white", driver);
        getFunc.clearPopUp(driver);
        getFunc.playListCheckForClear("Мыло SafeGuard white", driver);
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Перевірка валідності роботи кнопки 'Відібрати'",groups = {"filterPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void I_selectionButtonTest_9() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        String[] inputValues = {"1", "22", "333", "5689"};

        // check default condition - DISABLED
        getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
        getFunc.isFieldEmpty("vendorCode", driver);
        getFunc.isFieldEmpty("name", driver);
        getFunc.isFieldEmpty("provider", driver);
        getFunc.isElementEnabledAlert("providerList", driver);
        getFunc.isElementEnabledAlertByXpath("//form/div[3]/button[2]", driver);

        //Check button condition Enabled -> Disabled -> Enabled
        getFunc.inputValue(driver, "//div/div/input", inputValues[0]);
        getFunc.isElementDisabledAlertByXpath("//form/div[3]/button[2]", driver);

        try {
            driver.findElement(By.xpath("//div/div/input")).clear();
        } catch (Exception e){
            throw new RuntimeException("Не можливо очистити поле: Артикул");
        }


        getFunc.isElementEnabledAlertByXpath("//form/div[3]/button[2]", driver);
        getFunc.inputValue(driver, "//div/div/input", inputValues[0]);
        getFunc.isElementDisabledAlertByXpath("//form/div[3]/button[2]", driver);
        getFunc.clearPopUp(driver);

        //Check alert message + Button condition (Product field)
        for (int i = 0; i < 3; i++) {
            if (i < 2) {
                getFunc.inputValue(driver, "//form/div[1]/div[1]/input", inputValues[i]);
                getFunc.GetText("body > div:nth-child(3) > div > form > div:nth-child(2) > span",
                        "Введіть 3 або більше символів!", driver);
                try {
                    driver.findElement(By.xpath("//form/div[1]/div[1]/input")).clear();
                } catch (Exception e){
                    throw new RuntimeException("Не можливо очистити поле назва товару");
                }
            } else {
                getFunc.inputValue(driver, "//form/div[1]/div[1]/input", inputValues[i]);
                Thread.sleep(1000);

                getFunc.GetTextPresentAlert("body > div:nth-child(3) > div > form > " +
                        "div:nth-child(2) > span", "Введіть 3 або більше символів!", driver);
            }
        }
        getFunc.clearPopUp(driver);

        //Check alert message + Button condition (provider field)
        for (int i = 0; i < 3; i++) {
            if (i < 2) {
                getFunc.inputValue(driver, "//form/div[1]/div[3]/div/input", inputValues[i]);
                getFunc.GetText("body > div:nth-child(3) > div > form > div:nth-child(2) > span",
                        "Введіть 3 або більше символів!", driver);

                getFunc.isElementEnabledAlertByXpath("//form/div[1]/div[3]/select", driver);
                try {
                    driver.findElement(By.xpath("//div[3]/div/input")).clear();
                }catch (Exception e){
                    throw new RuntimeException("Не можливо очистити поле Постачальник");
                }
            } else {
                getFunc.inputValue(driver, "//form/div[1]/div[3]/div/input", inputValues[i]);

                Thread.sleep(1000);
                getFunc.GetTextPresentAlert("body > div:nth-child(3) > div > form > " +
                        "div:nth-child(2) > span", "Введіть 3 або більше символів!", driver);

                getFunc.isElementEnabledAlertByXpath("//form/div[1]/div[3]/select", driver);
                getFunc.clickSelectorByXpath(driver,"//div[3]/div/button","Не можливо натиснути кнопку (...)");
                getFunc.waitForElementByName("providerList", driver);
                getFunc.isElementDisabledAlertByXpath("//form/div[1]/div[3]/select", driver);
            }
        }
        getFunc.clearPopUp(driver);

        //Check alert message + Button condition (play list field)
        getFunc.inputValue(driver, "//form/div[1]/div[3]/div/input", inputValues[3]);
        Thread.sleep(1000);
        getFunc.GetTextPresentAlert("body > div:nth-child(3) > div > form > " +
                "div:nth-child(2) > span", "Введіть 3 або більше символів!", driver);

        getFunc.isElementEnabledAlertByXpath("//form/div[1]/div[3]/select", driver);
        getFunc.clickSelectorByXpath(driver,"//div[3]/div/button","Не можливо натиснути кнопку (...)");
        getFunc.waitForElementByXpath("//div[4]/div/p", 20, driver);
        getFunc.pageCheckForValue("Даних не знайдено !", driver);
        System.gc();
//        stopBrowser();

    }


    @Test(testName = "Перевірка поля 'Постачальник'", groups = {"filterPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void J_inputFieldProviderTest_10() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        String[] inputValues = {"1", "02", "/*-+Ж/\\^[a-z0-9_-]{3,1'5}$\"", "0,359", "'`ёёё", "текст", "і4і4і4і4і4і4і",
                "-02", "Постачальник постачальника постачае постачене", "Ріка", "(ріка)",
                "ёёё", "їїї", "ГУД-ФУД", "\"тов\"", "(тов)", "Хлібокомбінат №2"};

        // check default condition - DISABLED
        getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);

        for (int i = 0; i < inputValues.length; i++) {
            //verify disabled "..." button condition
            if (i < 5) {
                getFunc.inputValue(driver, "//div[3]/div/input", inputValues[i]);
                getFunc.isElementEnabledAlertByXpath("//div[3]/div/button", driver);
            }
            //verify message "Введіть 3 або більше символів!"
            if (i < 2) {
                getFunc.GetText("body > div:nth-child(3) > div > form > div:nth-child(2) > span",
                        "Введіть 3 або більше символів!", driver);
            }
            if (i > 5) {
                getFunc.inputValue(driver, "//div[3]/div/input", inputValues[i]);
                getFunc.isElementDisabledAlertByXpath("//div[3]/div/button", driver);
            }
            getFunc.clearPopUp(driver);
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Перевірка плейліста артикулів",groups = {"filterPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void K_playListArticulsTest_11() throws Exception {
        startBrowser();


        getFunc.catchStatusLoadPage();

        String[] inputValues = {"1", "125036\n" + "125037\n" + "125038\n" + "125039\n" + "125040\n" + "125041\n" +
                "125042\n" + "125043\n" +"125044\n" + "125045\n" + "125046\n" + "125047\n" + "125048\n" + "125049\n" +
                "125050\n" + "125051\n" + "125052\n" + "125053\n" + "125054\n" + "125055\n" + "125056\n" + "125057\n" +
                "125058\n" + "125059\n" + "125060\n" + "125061\n" + "125062\n" + "125063\n" + "125064\n",

                "125036\n" + "125037\n" + "125038\n" + "125039\n" + "125040\n" + "125041\n" + "125042\n" + "125043\n" +
                "125044\n" + "125045\n" + "125046\n" + "125047\n" + "125048\n" + "125049\n" + "125050\n" +
                "125051\n" + "125052\n" + "125053\n" + "125054\n" + "125055\n" + "125056\n" + "125057\n" +
                "125058\n" + "125059\n" + "125060\n" + "125061\n" + "125062\n" + "125063\n" + "125064\n",

                "123", "989638"};

        getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
        for (int i = 0; i < inputValues.length; i++) {
            if (i == 0) {
                getFunc.isFieldEmpty("vendorCode", driver);

                getFunc.sendKeysByXpath(driver,"//input",
                        "Не можливо ввести дані в поле артикул POP UP меню", inputValues[i]);

                getFunc.clickSelectorByXpath(driver,"//button[2]",
                        "Не можливо натиснути кнопку \"Відібрати\"");

                getFunc.waitForElementByXpath("html/body/div[3]/div/form/div[4]/div/ul/li/span[2]", 20, driver);
                getFunc.pageCheckForValue("Горчица Верес с хреном", driver);
                getFunc.clearPopUp(driver);
            }
            if (i > 0 && i < 3) {
                getFunc.inputMultiArticles(driver, inputValues[i]);
                getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                        "Не можливо натиснути кнопку Відібрати POP UP Меню");

                Thread.sleep(2500);
                getFunc.clickSelectorByClass(driver,"fa-chevron-right",
                        "Не можливо натиснути кнопку переключення на наступну сторінку");

                Thread.sleep(1500);
                getFunc.clickSelectorByClass(driver,"fa-chevron-left",
                        "Не можливо натиснути кнопку переключення на попередню сторінку");

                Thread.sleep(1500);
                getFunc.clickSelectorByClass(driver,"fa-angle-double-right",
                        "Не можливо натиснути кнопку переключення на останню сторінку");

                Thread.sleep(1500);
                getFunc.clickSelectorByClass(driver,"fa-angle-double-left",
                        "Не можливо натиснути кнопку переключення на першу сторінку");

                Thread.sleep(1500);
                getFunc.clearPopUp(driver);
            }
            if (i == 3) {
                getFunc.searchProvider(inputValues[i], driver);
                getFunc.waitForElementByXpath("//form/div[1]/div[3]/select",15,driver);

                try {
                    Select dropdown;
                    dropdown = new Select(driver.findElement(By.xpath("//form/div[1]/div[3]/select")));
                    dropdown.selectByVisibleText("БЛЕСК ЧП");
                }catch (Exception e){
                    throw new RuntimeException("Не можливо вибрати БЛЕСК ЧП");
                }
                //getFunc.pageCheckForValue("БЛЕСК ЧП", driver);
                getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                        "Не можливо натиснути кнопку Відібрати POP UP Меню");

                getFunc.waitForElementByXpath("html/body/div[3]/div/form/div[4]/div/ul/li/span[2]", 15,driver);
                getFunc.pageCheckForValue("Гирлянда пушистая №1", driver);
                getFunc.clearPopUp(driver);
            }
            if (i == 4) {
                getFunc.sendKeysByXpath(driver,"//input",
                        "Не можливо ввести дані в поле артикул POP UP меню", inputValues[i]);

                getFunc.inputValue(driver, "//form/div[1]/div[1]/input", inputValues[i]);
                getFunc.searchProvider(inputValues[i], driver);
                getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                        "Не можливо натиснути кнопку Відібрати POP UP Меню");

                getFunc.waitForElementByXpath("//form/div[4]/div/p", 15, driver);
                getFunc.pageCheckForValue("Даних не знайдено !", driver);
                getFunc.clearPopUp(driver);
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Перевірка кнопки 'Cancel'", groups = {"filterPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void L_cancelButtonTest_12() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        String[] inputValues = {"12", "мило", "123", "гвоздь"};


        getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
        for (int i = 0; i < inputValues.length; i++) {
            switch (i) {
                case 0:
                    getFunc.inputValue(driver, "//form/div[1]/div[1]/div/div/input", inputValues[i]);
                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");

                    getFunc.waitForElementByXpath("html/body/div[3]/div/form/div[4]/div/ul/li/span[2]", 20, driver);
                    getFunc.pageCheckForValue("Мыло Camay Chic", driver);
                    getFunc.clickSelectorByXpath(driver,"//form/div[6]/button[1]",
                            "Не можливо натиснути кнопку Cancel POP UP Меню");

                    Thread.sleep(1500);
                    getFunc.GetTextPresentAlert("body > div:nth-child(3) > div", "Мыло Camay Chic", driver);
                    break;
                case 1:
                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
                    getFunc.searchProductList(inputValues[i], driver);
                    getFunc.checkPlayListPagesForValues(driver, inputValues[i]);
                    getFunc.clickSelectorByXpath(driver,"//form/div[6]/button[1]",
                            "Не можливо натиснути кнопку Cancel POP UP Меню");

                    Thread.sleep(1500);
                    getFunc.GetTextPresentAlert("body > div:nth-child(3) > div", "мило", driver);
                    break;
                case 2:
                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
                    getFunc.searchProvider(inputValues[i], driver);
                    getFunc.waitForElementByXpath("//div[3]/button[2]", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");

                    getFunc.waitForElementByXpath("html/body/div[3]/div/form/div[4]/div/ul/li/span[2]", 40, driver);
                    getFunc.pageCheckForValue("Гирлянда пушистая №1", driver);
                    getFunc.clickSelectorByXpath(driver,"//form/div[6]/button[1]",
                            "Не можливо натиснути кнопку Cancel POP UP Меню");

                    Thread.sleep(1500);
                    getFunc.GetTextPresentAlert("body > div:nth-child(3) > div", "Гирлянда пушистая №1", driver);
                    break;
                case 3:
                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
                    getFunc.searchProductList(inputValues[i], driver);
                    getFunc.pageCheckForValue(inputValues[i], driver);
                    getFunc.clickSelectorByXpath(driver,"//form/div[6]/button[1]",
                            "Не можливо натиснути кнопку Cancel POP UP Меню");

                    Thread.sleep(1500);
                    getFunc.GetTextPresentAlert("body > div:nth-child(3) > div", "гвоздь", driver);
                    getFunc.clickSelectorByClass(driver,"article-cell","Не можливо відкрити POP UP меню");
                    getFunc.pageCheckForValue(inputValues[i], driver);
                    break;
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Перевірка кнопки 'ОК'",groups = {"filterPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void M_okButtonPopUpTest_13() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        String[] inputValues = {"23", "товар", "АЛЬФА", "",
                "125036\n" + "125037\n" + "125038\n" + "125039\n" + "125040\n" + "125041\n" + "125042\n" +
                        "125043\n" + "125044\n" + "125053\n",
                "8959", "154, 316, 317, 319 "};

        getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
        for (int i = 0; i < inputValues.length; i++) {
            switch (i) {
                case 0:
                    getFunc.inputValue(driver, "//form/div[1]/div[1]/div/div/input", inputValues[i]);
                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");

                    getFunc.waitForElementByXpath("//div/form/div[4]/div/ul/li/span[2]", 15, driver);
                    getFunc.pageCheckForValue("Клюква", driver);
                    break;
                case 1:
                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
                    getFunc.searchProductList(inputValues[i], driver);
                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");

                    getFunc.pageCheckForValue(inputValues[i], driver);
                    break;
                case 2:
                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
                    getFunc.searchProvider(inputValues[i], driver);

                    Thread.sleep(1500);
                    try {
                        driver.findElement(By.xpath("//select")).isEnabled();
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо перевірити випадаючий список постачальників на активність");
                    }
                    WebElement dropDownProvider = driver.findElement(By.xpath("//select"));
                    Select select = new Select(dropDownProvider);
                    select.selectByVisibleText("АЛЬФА-ПРИМА ООО");

                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");

                    getFunc.waitForElementByXpath("html/body/div[3]/div/form/div[4]/div/ul/li/span[2]", 15, driver);
                    getFunc.pageCheckForValue("Масло Коровка масляная Авис", driver);
                    break;
                case 3:
                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
                    getFunc.isElementEnabledAlertByXpath("//form/div[6]/button[2]", driver);
                    break;
                case 4:
                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
                    getFunc.inputMultiArticles(driver, inputValues[i]);
                    Thread.sleep(2000);
                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");

                    getFunc.waitForElementByXpath("html/body/div[3]/div/form/div[4]/div/ul/li/span[2]", 15,driver);
                    getFunc.pageCheckForValue("Хлеб Скифский", driver);
                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку (OK) POP UP меню");

                    Thread.sleep(1000);
                    getFunc.pageCheckForValue("Хлеб Скифский", driver);
                    break;
                case 5:
                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
                    getFunc.searchProvider(inputValues[i], driver);
                    getFunc.waitForElementByXpath("//form/div[4]/div/p", 20, driver);
                    getFunc.isElementEnabledAlertByXpath("//form/div[6]/button[2]", driver);
                    getFunc.pageCheckForValue("Даних не знайдено !", driver);
                    break;
                case 6:
                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
                    getFunc.inputMultiArticles(driver, inputValues[i]);
                    Thread.sleep(1000);
                    getFunc.clearPopUp(driver);
                    getFunc.inputMultiArticles(driver, inputValues[i]);
                    Thread.sleep(1000);
                    getFunc.isElementEnabledAlertByXpath("//form/div[6]/button[2]", driver);
                    break;
            }
            /**
             * використання зборщика мусора для очистки пам'яті
             */
            System.gc();
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Перевірка валідності початкової дати", groups = {"filterPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void N_startDateTest_14() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        /**
         * створення об'єкта для отримання значення поточної дати
         * формат дати - лише значення поточного місяця
         */
        DateFormat dateFormat = new SimpleDateFormat("MM");
        Date date = new Date();

        /**
         * змінна для корегування значення місяця (для правильного вибору значень з масива)
         */
        int correctionDate = 1;

        /**
         * значення місяця з урахуванням корегування
         */
        int monthCheckVal = Integer.parseInt(dateFormat.format(date)) - correctionDate;

        driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    getFunc.waitForElementByXpath("//div[1]/div[2]/span/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[1]/div[2]/span/button",
                            "Не можливо здійснити клік на кнопку (Дата початку періоду)");

                    if (Integer.parseInt(dateFormat.format(date)) > 1) {
                        // ranishe potochnogo perevirka na aktivnost
                        int correctionPrevMonth = 1;
                        for (int m = monthCheckVal - correctionPrevMonth; m >= 0; m--) {
                            getFunc.isElementEnabledAlertByXpath((monthXpath[m]), driver);
                        }
                    }
                    if (Integer.parseInt(dateFormat.format(date)) < 12) {
                        //pozje potochnogo mesjaca
                        int correctionNextMonth = 1;
                        for (int m = (monthCheckVal + correctionNextMonth); m < (monthXpath.length - correctionNextMonth); m++) {
                            getFunc.isElementEnabledAlertByXpath((monthXpath[m]), driver);
                        }
                    }
                    //perekluchenie na sledujushchi i predudushchi god
                    getFunc.clickSelectorByXpath(driver, "//th[1]/button",
                            "Не можливо натиснути кнопку переключення до попереднього місяця < (дата початку періоду)");

                    for (int k = 0; k < 12; k++) {
                        getFunc.isElementEnabledAlertByXpath((monthXpath[k]), driver);
                    }
                    getFunc.clickSelectorByXpath(driver,"//th[3]/button",
                            "Не можливо натиснути кнопку переключення до наступного місяця > (дата початку періоду)");

                    getFunc.clickSelectorByXpath(driver,"//th[3]/button",
                            "Не можливо натиснути кнопку переключення до наступного місяця > (дата початку періоду)");

                    for (int k = 0; k < 12; k++) {
                        getFunc.isElementEnabledAlertByXpath((monthXpath[k]), driver);
                    }
                    break;
                case 1:
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.waitForElementByXpath("//div[2]/div[2]/span/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[2]/span/button",
                            "Не можливо здійснити клік на кнопку (Дата закінчення періоду)");

                    Thread.sleep(1500);
                    if (Integer.parseInt(dateFormat.format(date)) < 12) {
                        /**
                         * змінна для корегування значення місяця (для правильного вибору значень з масива)
                         */
                        int correctionNextMonth  = 1;
                        getFunc.clickSelectorByXpath(driver,
                                (monthXpath[monthCheckVal + correctionNextMonth]),
                                String.format("Не можливо здійснити вибір місяця: %s",
                                        monthName[monthCheckVal + correctionNextMonth]));
                    }
                    getFunc.waitForElementByXpath("//div[1]/div[2]/span/button", 15, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[1]/div[2]/span/button",
                            "Не можливо здійснити клік на кнопку (Дата початку періоду)");

                    getFunc.isElementDisabledAlertByXpath((monthXpath[Integer.parseInt(dateFormat.format(date))]), driver);
                    getFunc.isElementDisabledAlertByXpath((monthXpath[Integer.parseInt(dateFormat.format(date)) + 1]), driver);
                    if (Integer.parseInt(dateFormat.format(date)) > 1) {
                        // ranishe potochnogo perevirka na aktivnost
                        /**
                         * змінна для корегування значення місяця використовуэться для перевірки попереднього місяця
                         */
                        int dateCorrectionVal = 2;
                        for (int m = monthCheckVal - dateCorrectionVal; m >= 0; m--) {
                            getFunc.isElementEnabledAlertByXpath((monthXpath[m]), driver);
                        }
                    }
                    if (Integer.parseInt(dateFormat.format(date)) < 12) {
                        //pozje potochnogo mesjaca
                        for (int m = (monthCheckVal + 3); m < (monthXpath.length - 1); m++) {
                            getFunc.isElementEnabledAlertByXpath((monthXpath[m]), driver);
                        }
                    }
                    break;
                case 2:
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.waitForElementByXpath("//div[1]/div[2]/span/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[1]/div[2]/span/button",
                            "Не можливо здійснити клік на кнопку (Дата початку періоду)");
                    Thread.sleep(1500);
                    try {
                        getFunc.isElementDisabledAlertByXpath((monthXpath[monthCheckVal]), driver);
                    } catch (Exception e){
                        throw new RuntimeException(
                                "Не можливо перевірити початок періоду на наявність не доступних елементів");
                    }
                    int monthCorrection = 1;
                    getFunc.isElementEnabledAlertByXpath((monthXpath[monthCheckVal - monthCorrection]), driver);
                    getFunc.isElementEnabledAlertByXpath((monthXpath[monthCheckVal + monthCorrection]), driver);

                    getFunc.clickSelectorByXpath(driver,"//li[2]/button",
                            "Не можливо натиснути кнопку Close форми вибору періоду");

                    getFunc.waitForElementByXpath("//div[2]/div[2]/span/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[2]/span/button",
                            "Не можливо здійснити клік на кнопку (Дата закінчення періоду)");

                    if (Integer.parseInt(dateFormat.format(date)) < 12) {
                        Thread.sleep(1500);
                        getFunc.clickSelectorByXpath(driver,
                                (monthXpath[monthCheckVal + monthCorrection]),
                                String.format("Не можливо здійснити вибір місяця: %s",
                                        monthName[monthCheckVal + monthCorrection]));
                    }
                    getFunc.waitForElementByXpath("//div[1]/div[2]/span/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[1]/div[2]/span/button",
                            "Не можливо здійснити клік на кнопку (Дата початку періоду)");

                    if(monthCheckVal > 1){
                        getFunc.isElementEnabledAlertByXpath((monthXpath[monthCheckVal - monthCorrection]), driver);
                    }
                    break;

                case 3:
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.waitForElementByXpath("//div[1]/div[2]/span/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[1]/div[2]/span/button",
                            "Не можливо здійснити клік на кнопку (Дата початку періоду)");

                    getFunc.waitForElementByXpath("//th[1]/button", 8, driver);
                    for (int k = 5; k > 0; k--) {
                        getFunc.clickSelectorByXpath(driver, "//th[1]/button",
                                "Не можливо натиснути кнопку переключення до попереднього місяця < (дата початку періоду)");

                        Thread.sleep(1500);
                    }
                    getFunc.clickSelectorByXpath(driver,"//li[2]/button",
                            "Не можливо натиснути кнопку Close форми вибору періоду");

                    DateFormat currentYear = new SimpleDateFormat("yyyy");
                    Date year = new Date();
                    try {
                        WebElement datePickerStartValue = driver.findElement(By.xpath("//div[3]/div[1]/div[1]/input"));
                        String tmp = datePickerStartValue.getAttribute("value");
                        if (!tmp.equalsIgnoreCase(monthName[monthCheckVal] + " - " + currentYear.format(year))) {
                            throw new RuntimeException("Початкова дата відрізняється від заданої !");
                        }
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо здійснити зчитування дати початку періоду ");
                    }
                    break;
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Перевірка валідності кінцевої дати", groups = {"filterPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void O_endDateTest_15() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        /**
         * створення об'єкта для отримання значення поточної дати
         * формат дати - лише значення поточного місяця
         */
        DateFormat dateFormat = new SimpleDateFormat("MM");
        Date date = new Date();

        /**
         * змінна для корегування значення місяця (для правильного вибору значень з масива)
         */
        int correctionDate = 1;

        /**
         * значення місяця з урахуванням корегування
         */
        int monthCheckVal = Integer.parseInt(dateFormat.format(date)) - correctionDate;

        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.waitForElementByXpath("//div[2]/div[2]/span/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[2]/span/button",
                            "Не можливо здійснити клік на кнопку (Дата закінчення періоду)");

                    getFunc.waitForElementByXpath("//th[1]/button", 8, driver);
                    for (int k = 5; k > 0; k--) {
                        getFunc.clickSelectorByXpath(driver, "//th[1]/button",
                                "Не можливо натиснути кнопку переключення до попереднього місяця < (дата початку періоду)");
                        Thread.sleep(1500);
                    }
                    getFunc.clickSelectorByXpath(driver,"//li[2]/button",
                            "Не можливо натиснути кнопку Close форми вибору періоду");

                    DateFormat currentYear = new SimpleDateFormat("yyyy");
                    Date year = new Date();
                    try {
                        /** оголошення веб елемента в який буде вміщати значення дати*/
                        WebElement datePickerStartValue = driver.findElement(By.xpath("//div[3]/div[1]/div[1]/input"));
                        /** змінна який присвоюватиметься текстове значення дати*/
                        String tmp = datePickerStartValue.getAttribute("value");
                        if (!tmp.equalsIgnoreCase(monthName[monthCheckVal] + " - " + currentYear.format(year))) {
                            throw new RuntimeException("Початкова дата відрізняється від заданої !");
                        }
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо здійснити зчитування дати початку періоду ");
                    }
                    break;
                case 1:
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    int dateCount = 0;
                    int monthCount = monthCheckVal;
                    if (monthCheckVal > 3) {
                        dateCount = 11 - monthCount;
                    }
                    for (int j = dateCount; j > 0; j--) {
                        getFunc.waitForElementByXpath("//div[2]/div[2]/span/button", 10, driver);
                        getFunc.clickSelectorByXpath(driver,"//div[2]/div[2]/span/button",
                                "Не можливо здійснити клік на кнопку (Дата закінчення періоду)");

                        getFunc.waitForElementByXpath(monthXpath[monthCount++], 10, driver);
                        getFunc.isElementDisabledAlertByXpath(monthXpath[monthCount], driver);

                        getFunc.clickSelectorByXpath(driver,
                                (monthXpath[monthCount]), String.format("Не можливо здійснити вибір місяця: %s",
                                        monthName[monthCount]));
                    }
                    break;
                case 2:
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.waitForElementByXpath("//div[2]/div[2]/span/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[2]/span/button",
                            "Не можливо здійснити клік на кнопку (Дата закінчення періоду)");

                    getFunc.waitForElementByXpath("//th[3]/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//th[3]/button",
                            "Не можливо натиснути кнопку переключення до наступного місяця > (дата початку періоду)");

                    for (int k = 0; k < (11 - monthCheckVal); k++) {
                        getFunc.waitForElementByXpath(monthXpath[k], 10, driver);
                        getFunc.isElementDisabledAlertByXpath(monthXpath[k], driver);
                    }
                    for (int p = getFunc.returnDate(monthCheckVal); p < 12; p++) {
                        Thread.sleep(1500);
                        getFunc.isElementEnabledAlertByXpath(monthXpath[p], driver);
                    }
                    break;
                case 3:
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.waitForElementByXpath("//div[2]/div[2]/span/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[2]/span/button",
                            "Не можливо здійснити клік на кнопку (Дата закінчення періоду)");

                    getFunc.waitForElementByXpath(monthXpath[monthCheckVal], 10, driver);
                    getFunc.isElementEnabledAlertByXpath(monthXpath[monthCheckVal - correctionDate], driver);
                    break;
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Перевірка кнопки 'Застосувати дати'", groups = {"filterPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void Q_applyDataButtonTest_16() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
        Thread.sleep(3500);
        getFunc.clickSelectorByXpath(driver, "//month-picker/div/div[4]/button",
                "неможливо натиснути кнопку панелі фільтрів: (Застосувати дати)");
        /** змінна в яку присвоююється текстове значення артикула з панелі фільтрів*/
        String checkField;
        try{
            checkField = driver.findElement(By.xpath("//div/div/div[3]")).getText();
        } catch (Exception e){
            throw new RuntimeException("Не можливо скопіювати вміст поля Артикул панелі фільтрів");
        }

        if (!checkField.equalsIgnoreCase("")) {
            throw new RuntimeException("Поле артикул не пусте !!!");
        }
        for (int i = 0; i < 4; i++){
            switch (i) {
                case 0: {
                    getFunc.getSearchArticle("2334", driver);
                    try{
                        checkField = driver.findElement(By.xpath("//div/div/div[3]")).getText();
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо скопіювати вміст поля Артикул панелі фільтрів");
                    }
                    if(checkField.equalsIgnoreCase("")){
                        throw new RuntimeException("Не відображається артикул на панелі фільтрів");
                    }
                    Thread.sleep(3500);
                    getFunc.clickSelectorByXpath(driver, "//month-picker/div/div[4]/button",
                            "неможливо натиснути кнопку панелі фільтрів: (Застосувати дати)");
                    Thread.sleep(1500);
                    /** Булієвська змінна для присвоєння стану перевірки наявності інфоблоку*/
                    boolean elementStatus;
                    elementStatus = !driver.findElements(By.cssSelector(".info-wrap.active")).isEmpty();
                    if (!elementStatus) {
                        throw new RuntimeException("Інфоблок не активний !!!");
                    }
                    break;
                }
                case 1: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle("2334", driver);
                    try{
                        checkField = driver.findElement(By.xpath("//div/div/div[3]")).getText();
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо скопіювати вміст поля Артикул панелі фільтрів");
                    }
                    if(checkField.equalsIgnoreCase("")){
                        throw new RuntimeException("Не відображається артикул на панелі фільтрів");
                    }

                    Thread.sleep(3500);
                    getFunc.clickSelectorByXpath(driver, "//month-picker/div/div[4]/button",
                            "неможливо натиснути кнопку панелі фільтрів: (Застосувати дати)");

                    Thread.sleep(1500);
                    /** Булієвська змінна для присвоєння стану перевірки наявності інфоблоку*/
                    boolean elementStatus;
                    elementStatus = !driver.findElements(By.cssSelector(".info-wrap.active")).isEmpty();
                    if (!elementStatus) {
                        throw new RuntimeException("Інфоблок не активний");
                    }
                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
                    getFunc.clearPopUp(driver);
                    Thread.sleep(3500);
                    getFunc.clickSelectorByXpath(driver, "//month-picker/div/div[4]/button",
                            "неможливо натиснути кнопку панелі фільтрів: (Застосувати дати)");

                    elementStatus = !driver.findElements(By.cssSelector(".info-wrap.active")).isEmpty();
                    if (elementStatus) {
                        throw new RuntimeException("Інфоблок активний після очистки поля артикул");
                    }
                    break;
                }
                case 2: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    /** Булієвська змінна для присвоєння стану перевірки наявності інфоблоку*/
                    boolean elementStatus;
                    Thread.sleep(3500);
                    getFunc.clickSelectorByXpath(driver, "//month-picker/div/div[4]/button",
                            "неможливо натиснути кнопку панелі фільтрів: (Застосувати дати)");

                    elementStatus = !driver.findElements(By.cssSelector(".info-wrap.active")).isEmpty();
                    if (elementStatus) {
                        throw new RuntimeException("Інфоблок активний після очистки поля артикул");
                    }
                    break;
                }
                case 3: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle("2334", driver);
                    try{
                        checkField = driver.findElement(By.xpath("//div/div/div[3]")).getText();
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо скопіювати вміст поля Артикул панелі фільтрів");
                    }
                    if(checkField.equalsIgnoreCase("")){
                        throw new RuntimeException("Не відображається артикул на панелі фільтрів");
                    }

                    Thread.sleep(3500);
                    getFunc.clickSelectorByXpath(driver, "//month-picker/div/div[4]/button",
                            "неможливо натиснути кнопку панелі фільтрів: (Застосувати дати)");

                    boolean elementStatus;
                    Thread.sleep(3500);
                    getFunc.clickSelectorByXpath(driver, "//month-picker/div/div[4]/button",
                            "неможливо натиснути кнопку панелі фільтрів: (Застосувати дати)");

                    elementStatus = !driver.findElements(By.cssSelector(".info-wrap.active")).isEmpty();
                    if (!elementStatus) {
                        throw new RuntimeException("Інфоблок не активний");
                    }
                    getFunc.waitForElementByXpath("//month-picker/div/div[4]/button", 15, driver);
                    getFunc.clickSelectorByXpath(driver, "//month-picker/div/div[4]/button",
                            "неможливо натиснути кнопку панелі фільтрів: (Застосувати дати)");

                    elementStatus = !driver.findElements(By.cssSelector(".info-wrap.active")).isEmpty();
                    if (!elementStatus) {
                        throw new RuntimeException("Інфоблок не активний");
                    }
                    break;
                }
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Перевірка елемента(прогноз), інфоблок", groups = {"infoPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void R_checkForecastElementTest_17() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        String[] inputValues = {"","рошен","рошен","рошен","рошен","рошен", "рошен","136","18"};

        driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

        /** змінна для присвоєння текстового вмісту поля */
        String checkFilterPanelVal;

        for(int i = 0; i < 9; i++){
            switch (i){
                case 0: {
                    getFunc.clickSelectorByXpath(driver, "//month-picker/div/div[4]/button",
                            "неможливо натиснути кнопку панелі фільтрів: (Застосувати дати)");

                    try {
                        checkFilterPanelVal = driver.findElement(By.xpath("//div/div/div[3]")).getText();
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо звернутись до поля артикул, панелі фільтрів");
                    }
                    if(!checkFilterPanelVal.equalsIgnoreCase("")){
                        throw new RuntimeException("Поле артикул не пусте");
                    }
                    /** Булієвська змінна для присвоєння стану перевірки наявності інфоблоку*/
                    boolean elementStatus;
                    elementStatus = !driver.findElements(By.cssSelector(".info-wrap.active")).isEmpty();
                    if (elementStatus) {
                        throw new RuntimeException("Інфоблок активний без пошуку артикула!");
                    }
                    break;
                }

                case 1: {
                    getFunc.openDesktopWithArticleByXpath(driver,"//section[1]/div/div[1]/div/div",
                            inputValues[i], "Прогноз", baseUrl, userLogin, userPassword, currentDomain);
                    break;
                }

                case 2:{
                    getFunc.openDesktopWithArticleByXpath(driver,"//section[1]/div/div[1]/div/div",
                            inputValues[i], "Прогноз", baseUrl, userLogin, userPassword, currentDomain);
                    /** Текстова змінна для присвоєння значення прогнозу з інфоблоку*/
                    String forecastVal = getFunc.getFieldTextByXpath(driver,
                            "//section[1]/div/div[1]/div/div");

                    for (int j = 0; j < 3; j++){
                        getFunc.waitForElementByXpath("//div[4]/button/i", 8, driver);
                        getFunc.clickSelectorByXpath(driver,"//div[4]/button/i",
                                "не можливо натиснути кнопку перехід до наступного артикула (>)");
                        Thread.sleep(1500);
                    }
                    for (int k = 0; k < 3; k++){
                        getFunc.waitForElementByXpath("//div/div[2]/button", 8, driver);
                        getFunc.clickSelectorByXpath(driver,"//div/div[2]/button",
                                "не можливо натиснути кнопку перехід до наступного артикула (<)");
                        Thread.sleep(1500);
                    }

                    String compareForecastVal;

                    try {
                        compareForecastVal = (getFunc.getFieldTextByXpath(driver,
                                "//section[1]/div/div[1]/div/div"));
                    }catch (Exception e){
                        throw new RuntimeException("Неможливо отримати інтове значення робочого столу Прогноз");
                    }

                    if(!forecastVal.equalsIgnoreCase(compareForecastVal)){
                        throw new RuntimeException("Після переключення артикулів, " +
                                "не відбулось повернення до початокового значення");
                    }
                    break;
                }

                case 3:{
                    getFunc.openDesktopWithArticleByXpath(driver,"//section[1]/div/div[1]/div/div",
                            inputValues[i], "Прогноз", baseUrl, userLogin, userPassword, currentDomain);
                    /** Текстова змінна для присвоєння значення прогнозу з інфоблоку*/
                    String forecastVal = getFunc.getFieldTextByXpath(driver,
                            "//section[1]/div/div[1]/div/div");

                    Thread.sleep(3500);
                    getFunc.clickSelectorByXpath(driver,"//div/div[5]/button/i",
                            "не можливо натиснути кнопку перехід до наступного артикула (>>)");
                    Thread.sleep(1500);

                    getFunc.clickSelectorByXpath(driver,"//div[2]/div/div/div[1]/button/i",
                            "не можливо натиснути кнопку перехід до наступного артикула (<<)");
                    Thread.sleep(1500);

                    /** Текстова змінна для присвоєння значення прогнозу з інфоблоку після переключення артикула*/
                    String compareForecastVal = getFunc.getFieldTextByXpath(driver,
                            "//section[1]/div/div[1]/div/div");
                    if(!forecastVal.equalsIgnoreCase(compareForecastVal)){
                        throw new RuntimeException("Після переключення артикулів, " +
                                "не відбулось повернення до початокового значення");
                    }
                    break;
                }

                case 4:{
                    getFunc.openDesktopWithArticleByXpath(driver,"//section[1]/div/div[1]/div/div",
                            inputValues[i], "Прогноз", baseUrl, userLogin, userPassword, currentDomain);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");

                    Thread.sleep(1500);
                    getFunc.checkForDeskTopOpen(driver,"//section[2]/div/div");
                    break;
                }

                case 5:{
                    getFunc.openDesktopWithArticleByXpath(driver,"//section[1]/div/div[1]/div/div",
                            inputValues[i], "Прогноз", baseUrl, userLogin, userPassword, currentDomain);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");

                    Thread.sleep(1500);
                    getFunc.checkForDeskTopOpen(driver,"//section[2]/div/div");

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");

                    Thread.sleep(1500);
                    getFunc.checkForDeskTopClose(driver, "//section[2]/div/div");
                    break;
                }

                case 6:{
                    getFunc.openDesktopWithArticleByXpath(driver,"//section[1]/div/div[1]/div/div",
                            inputValues[i], "Прогноз", baseUrl, userLogin, userPassword, currentDomain);
                    getFunc.clickSelectorByXpath(driver,"//div/div/div[3]",
                            "Не можливо відкрити POP UP Menu");

                    Thread.sleep(1500);
                    getFunc.clearPopUp(driver);

                    Thread.sleep(1500);
                    getFunc.checkForDeskTopClose(driver, "//section[2]/div/div");
                    break;
                }

                case 7:{
                    getFunc.catchStatusLoadPage();

                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValues[i], driver);

                    try {
                        assertEquals("Даних немає", getFunc.getFieldTextByXpath(driver,"//section[1]/div/div[1]/div/div"));
                    } catch (Exception e){
                        throw new RuntimeException("Робочий стіл не відображає повідомлення (Даних немає)");
                    }
                    break;
                }

                case 8:{
                    getFunc.catchStatusLoadPage();

                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValues[i], driver);
                    int forecastVal;
                    try {
                        getFunc.waitForElementByXpath("//section[1]/div/div[1]/div/div", 10, driver);
                        forecastVal = Integer.parseInt(getFunc.getFieldTextByXpath(driver,
                                "//section[1]/div/div[1]/div/div").replace(" ", ""));
                    }catch (Exception e){
                        throw new RuntimeException("Неможливо отримати інтове значення робочого столу Прогноз");
                    }

                    if(forecastVal <= 0){
                        throw new RuntimeException("Значеня робочого смтолу Прогноз <=0 ");
                    }
                    break;
                }

            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Перевірка елемента(акції), інфоблок", groups = {"infoPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void S_checkSharesElementTest_18() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        String[] inputValues = {"1158", "40574", "40574\n1158\n136", "136", "136, 40574", "", "1158",
                "1158", "1158", "1158"};
        String sharesName = "(Ф) Газета №23 (30.11-13.12.16)";

        for(int i = 0; i < 10; i++){
            switch (i){
                case 0:{
                    getFunc.catchStatusLoadPage();

                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValues[i], driver);

                    Float sharesVal;
                    try {
                        sharesVal = Float.parseFloat(getFunc.getFieldTextByXpath(driver,
                                "//section[1]/div/div[2]/div/div[1]").replace(" ","").replace(",","."));
                    }catch (Exception e){
                        throw new RuntimeException("Неможливо отримати числове значення інфоблоку Акції");
                    }
                    if(sharesVal <= 0){
                        throw new RuntimeException("Значеня робочого смтолу Акції <=0 ");
                    }

                    getFunc.checkContainsValueByXpath(driver,"//section[1]/div/div[2]/div/div[2]/div[1]/p[2]",
                            sharesName);

                    DateFormat dateFormat = new SimpleDateFormat("MM.yyyy");
                    Date date = new Date();

                    /** Текстова змінна для отримання значення поточного місяця і поточного року в форматі MM.yyyy*/
                    String currentDate = dateFormat.format(date);

                    getFunc.checkContainsValueByXpath(driver,"//section[1]/div/div[2]/div/div[2]/div[2]/p[2]",
                            currentDate);
                    break;
                }
                case 1:{
                    getFunc.catchStatusLoadPage();

                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.checkForInfoBlockEnabled(driver);

                    getFunc.checkContainsValueByXpath(driver,"//p[2]","кілька");
                    getFunc.checkContainsValueByXpath(driver,"//div[2]/p[2]","кілька");

                    break;
                }
                case 2:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.clickSelectorByXpath(driver,"//div/div/div[3]",
                            "Не можливо відкрити POP UP Menu");
                    getFunc.waitForElementByXpath("//div/div/div/div/div", 8, driver);
                    getFunc.inputMultiArticles(driver, inputValues[i]);
                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");
                    Thread.sleep(1500);
                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");

                    getFunc.waitForElementByXpath("//section[1]/div/div[2]/div/div[1]", 10,driver);
                    getFunc.checkForInfoBlockEnabled(driver);
                    getFunc.checkContainsValueByXpath(driver,"//section[1]/div/div[2]/div/div[1]","Даних немає");

                    getFunc.clickSelectorByXpath(driver,"//div[4]/button/i",
                            "не можливо натиснути кнопку перехід до наступного артикула (>)");
                    Thread.sleep(2000);

                    //getFunc.checkContainsValueByXpath(driver,"//section[1]/div/div[2]/div/div[1]","15 022");
                    getFunc.clickSelectorByXpath(driver,"//div[4]/button/i",
                            "не можливо натиснути кнопку перехід до наступного артикула (>)");

                    Thread.sleep(2000);
                    getFunc.checkContainsValueByXpath(driver,"//div[2]/p[2]","кілька");

                    break;
                }
                case 3:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.checkForInfoBlockEnabled(driver);
                    getFunc.checkContainsValueByXpath(driver,"//section[1]/div/div[2]/div/div[1]","Даних немає");
                    break;
                }
                case 4:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.clickSelectorByXpath(driver,"//div/div/div[3]",
                            "Не можливо відкрити POP UP Menu");
                    getFunc.waitForElementByXpath("//div/div/div/div/div", 8, driver);
                    getFunc.inputMultiArticles(driver, inputValues[i]);
                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");
                    Thread.sleep(1500);
                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");

                    getFunc.waitForElementByXpath("//section[1]/div/div[2]/div/div[1]", 8, driver);
                    getFunc.checkForInfoBlockEnabled(driver);
                    getFunc.checkContainsValueByXpath(driver,"//section[1]/div/div[2]/div/div[1]","Даних немає");

                    getFunc.clickSelectorByXpath(driver,"//div/div[5]/button/i",
                            "не можливо натиснути кнопку перехід до наступного артикула (>>)");
                    Thread.sleep(2000);

                    //getFunc.checkContainsValueByXpath(driver,"//section[1]/div/div[2]/div/div[1]","1 600");
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div/div/div[1]/button/i",
                            "не можливо натиснути кнопку перехід до наступного артикула (<<)");

                    Thread.sleep(2000);
                    getFunc.checkContainsValueByXpath(driver,"//section[1]/div/div[2]/div/div[1]","Даних немає");
                    break;
                }
                case 5:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    Thread.sleep(1500);

                    getFunc.clickSelectorByXpath(driver, "//month-picker/div/div[4]/button",
                            "неможливо натиснути кнопку панелі фільтрів: (Застосувати дати)");
                    Thread.sleep(1500);

                    getFunc.checkForInfoBlockDisabled(driver);
                    break;
                }
                case 6:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);


                    getFunc.clickSelectorByXpath(driver,"//div/div/div[3]",
                            "Не можливо відкрити POP UP Menu");

                    getFunc.waitForElementByXpath("//div[3]/button[1]", 8, driver);

                    getFunc.clearPopUp(driver);

                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");

                    getFunc.checkForInfoBlockDisabled(driver);
                    break;
                }
                case 7:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на робочий стіл Акції");

                    getFunc.waitForElementByXpath("//section[2]/div/div", 8,driver);

                    getFunc.isElementDisabledAlertByXpath("//section[2]/div/div", driver);
                    break;
                }
                case 8:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на робочий стіл Акції");

                    getFunc.waitForElementByXpath("//section[2]/div/div", 10, driver);

                    getFunc.isElementDisabledAlertByXpath("//section[2]/div/div", driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на робочий стіл Акції");

                    if(driver.findElements(By.cssSelector(".desktop.ng-scope")).isEmpty()){
                        throw new RuntimeException("Робочий стіл Акції не закритий");
                    }
                    break;
                }
                case 9:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на робочий стіл Акції");

                    getFunc.waitForElementByXpath("//section[2]/div/div", 10,driver);

                    getFunc.isElementDisabledAlertByXpath("//section[2]/div/div", driver);

                    getFunc.clickSelectorByXpath(driver,"//div/div/div[3]",
                            "Не можливо відкрити POP UP Menu");

                    getFunc.waitForElementByXpath("//div[3]/button[1]", 10, driver);

                    getFunc.clearPopUp(driver);

                    if(driver.findElements(By.cssSelector(".desktop.ng-scope")).isEmpty()){
                        throw new RuntimeException("Робочий стіл Акції не закритий");
                    }
                    break;
                }
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Перевірка елемента(похибка), інфоблок", groups = {"infoPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void T_checkErrorsElementTest_19() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        String[] inputValues = {"", "136", "18", "136", "18,21,24,25", "18,21,24,25", "136"};
        for(int i = 0; i < 7; i++) {
            switch (i) {
                case 0: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.waitForElementByXpath("//section[1]/div/div[1]/div", 10, driver);

                    getFunc.clickSelectorByXpath(driver, "//month-picker/div/div[4]/button",
                            "неможливо натиснути кнопку панелі фільтрів: (Застосувати дати)");
                    Thread.sleep(1500);
                    getFunc.checkForInfoBlockDisabled(driver);
                    break;
                }
                case 1: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.catchStatusLoadPage();
                    getFunc.waitForElementByXpath("//section[1]/div/div[1]/div", 10, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.isElementDisabledAlertByXpath("//section[1]/div/div[3]", driver);

                    getFunc.checkContainsValueByXpath(driver,"//section[1]/div/div[3]/div/div", "Даних немає");
                    break;
                }
                case 2: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.catchStatusLoadPage();
                    getFunc.waitForElementByXpath("//section[1]/div/div[1]/div", 10, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.isElementDisabledAlertByXpath("//section[1]/div/div[3]", driver);

                    /** Інтове значення помилки з інфоблоку без роздільних знаків*/
                    int errorVal;
                    try {
                        errorVal = Integer.parseInt(getFunc.getFieldTextByXpath(driver,
                                "//section[1]/div/div[3]/div/div").replace(" ","").replace("%","").replace(".",""));
                    }catch (Exception e){
                        throw new RuntimeException("Неможливо отримати інтове значення робочого столу Акції");
                    }
                    if(errorVal <= 0){
                        throw new RuntimeException("Значеня робочого смтолу Похибка <=0 ");
                    }
                    break;
                }
                case 3: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.checkForInfoBlockEnabled(driver);

                    getFunc.clickSelectorByXpath(driver,"//div/div/div[3]",
                            "Не можливо відкрити POP UP Menu");

                    getFunc.waitForElementByXpath("//div[3]/button[1]", 10, driver);

                    getFunc.clearPopUp(driver);

                    if(driver.findElements(By.cssSelector(".desktop.ng-scope")).isEmpty()){
                        throw new RuntimeException("Робочий стіл Акції не закритий");
                    }
                    break;
                }
                case 4:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.clickSelectorByXpath(driver,"//div/div/div[3]",
                            "Не можливо відкрити POP UP Menu");
                    getFunc.waitForElementByXpath("//div/div/div/div/div", 10, driver);

                    getFunc.inputMultiArticles(driver, inputValues[i]);

                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");

                    Thread.sleep(1500);
                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");

                    getFunc.waitForElementByXpath("//div/div[5]/button/i", 8, driver);
                    getFunc.clickSelectorByXpath(driver,"//div/div[5]/button/i",
                            "не можливо натиснути кнопку перехід до наступного артикула (>>)");

                    float floatErrorVal;
                    try {
                        WebElement errorValStart = driver.findElement(By.xpath("//section[1]/div/div[3]/div/div"));
                        String errorVal = errorValStart.getText().replace(" ","").replace("%", "");
                        floatErrorVal = Float.parseFloat(errorVal);
                    } catch (Exception e) {
                        throw new RuntimeException("Не можливо отримати числове значення похибки");
                    }
                    if(floatErrorVal < 0){
                        throw new RuntimeException("Значення похибки меньше нуля");
                    }
                    getFunc.waitForElementByXpath("//div/div[5]/button/i", 8, driver);

                    getFunc.clickSelectorByXpath(driver,"//div[2]/div/div/div[1]/button/i",
                            "не можливо натиснути кнопку перехід до наступного артикула (<<)");

                    try {
                        WebElement errorValStart = driver.findElement(By.xpath("//section[1]/div/div[3]/div/div"));
                        String errorVal = errorValStart.getText().replace(" ","").replace("%", "");
                        floatErrorVal = Float.parseFloat(errorVal);
                    } catch (Exception e) {
                        throw new RuntimeException("Не можливо отримати числове значення похибки");
                    }
                    if(floatErrorVal < 0){
                        throw new RuntimeException("Значення похибки меньше нуля");
                    }

                    break;
                }
                case 5:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.clickSelectorByXpath(driver,"//div/div/div[3]",
                            "Не можливо відкрити POP UP Menu");
                    getFunc.waitForElementByXpath("//div/div/div/div/div", 8, driver);

                    getFunc.inputMultiArticles(driver, inputValues[i]);

                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");

                    Thread.sleep(1500);
                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");

                    /** Початкове інтове значення помилки до зміни артикула*/
                    int errorValStart;
                    /** Кінцеве інтове значення помилки після зміни артикула */
                    int errorValEnd;
                    for (int j = 0; j < 3; j++){
                        try {
                            errorValStart = Integer.parseInt(getFunc.getFieldTextByXpath(driver,
                                    "//section[1]/div/div[3]/div/div").replace(" ","").replace("%","").replace(".",""));
                        }catch (Exception e){
                            throw new RuntimeException("Неможливо отримати інтове значення робочого столу Акції");
                        }
                        if(errorValStart <= 0){
                            throw new RuntimeException("Значеня робочого смтолу Похибка <=0 ");
                        }
                        getFunc.clickSelectorByXpath(driver,"//div[4]/button/i",
                                "не можливо натиснути кнопку перехід до наступного артикула (>)");
                        Thread.sleep(1500);
                        try {
                            errorValEnd = Integer.parseInt(getFunc.getFieldTextByXpath(driver,
                                    "//section[1]/div/div[3]/div/div").replace(" ","").replace("%","").replace(".",""));
                        }catch (Exception e){
                            throw new RuntimeException("Неможливо отримати інтове значення робочого столу Акції");
                        }
                        if(errorValEnd <= 0){
                            throw new RuntimeException("Значеня робочого смтолу Похибка <=0 ");
                        }

                        if(errorValEnd == errorValStart && j != 2){
                            throw new RuntimeException("Зміна значення похибки при переключенні артикулів не відбувається!");
                        }
                    }
                    for (int k = 0; k < 3; k++){
                        try {
                            errorValStart = Integer.parseInt(getFunc.getFieldTextByXpath(driver,
                                    "//section[1]/div/div[3]/div/div").replace(" ","").replace("%","").replace(".",""));
                        }catch (Exception e){
                            throw new RuntimeException("Неможливо отримати інтове значення робочого столу Акції");
                        }
                        if(errorValStart <= 0){
                            throw new RuntimeException("Значеня робочого смтолу Похибка <=0 ");
                        }
                        getFunc.clickSelectorByXpath(driver,"//div/div[2]/button",
                                "не можливо натиснути кнопку перехід до наступного артикула (<)");
                        Thread.sleep(1500);
                        try {
                            errorValEnd = Integer.parseInt(getFunc.getFieldTextByXpath(driver,
                                    "//section[1]/div/div[3]/div/div").replace(" ","").replace("%","").replace(".",""));
                        }catch (Exception e){
                            throw new RuntimeException("Неможливо отримати інтове значення робочого столу Акції");
                        }
                        if(errorValEnd <= 0){
                            throw new RuntimeException("Значеня робочого смтолу Похибка <=0 ");
                        }

                        if(errorValEnd == errorValStart && k != 2){
                            throw new RuntimeException("Зміна значення похибки при переключенні артикулів не відбувається!");
                        }
                    }
                    break;
                }
                case 6:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[3]",
                            "Не можливо знайти інфоблок - Похибка");

                    getFunc.checkForDeskTopClose(driver,"//section[2]/div/div");
                    break;
                }
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Перевірка елемента(група), інфоблок", groups = {"infoPanel"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void U_checkGroupElementTest_20() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        String[] inputValues = {"", "154", "136", "21,37,45", "21,37,45", "154", "317"};
        for (int i = 0; i < 7; i++) {
            switch (i) {
                case 0: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.waitForElementByXpath("//section[1]/div/div[1]/div", 10, driver);

                    getFunc.clickSelectorByXpath(driver, "//month-picker/div/div[4]/button",
                            "неможливо натиснути кнопку панелі фільтрів: (Застосувати дати)");
                    Thread.sleep(1500);
                    getFunc.checkForInfoBlockDisabled(driver);
                    break;
                }
                case 1:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.checkContainsValueByXpath(driver, "//section[1]/div/div[4]/div/div[1]","B");
                    getFunc.checkContainsValueByXpath(driver, "//section[1]/div/div[4]/div/div[2]/div[1]/p[2]",
                            "Gamma");
                    getFunc.checkContainsValueByXpath(driver, "//section[1]/div/div[4]/div/div[2]/div[2]/p[2]",
                            "Z");
                    break;
                }
                case 2: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.waitForElementByXpath("//section[1]/div/div[1]/div", 10, driver);
                    break;
                }
                case 3:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.waitForElementByXpath("//section[1]/div/div[1]/div", 10, driver);

                    getFunc.clickSelectorByXpath(driver,"//div/div/div[3]",
                            "Не можливо відкрити POP UP Menu");
                    getFunc.waitForElementByXpath("//div/div/div/div/div", 10, driver);

                    getFunc.inputMultiArticles(driver, inputValues[i]);

                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");

                    Thread.sleep(1500);
                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");

                    /** Оголошення змінних для отримання значень Групи - початкові значення до зміни артикула */
                    String startGroupVal, startABYanalizeVal, startXYZanalizeVal;
                    /** Оголошення змінних для отримання значень Групи - початкові значення до зміни артикула */
                    String endGroupVal, endABYanalizeVal,  endXYZanalizeVal;

                    startGroupVal = getFunc.getFieldTextByXpath(driver, "//div/div[4]/div/div[1]");
                    startABYanalizeVal = getFunc.getFieldTextByXpath(driver, "//div[4]/div/div[2]/div[1]/p[2]");
                    startXYZanalizeVal = getFunc.getFieldTextByXpath(driver, "//div[4]/div/div[2]/div[2]/p[2]");

                    for(int j = 0; j < 2; j++){
                        getFunc.clickSelectorByXpath(driver,"//div[4]/button/i",
                                "не можливо натиснути кнопку перехід до наступного артикула (>)");
                        endGroupVal = getFunc.getFieldTextByXpath(driver, "//div/div[4]/div/div[1]");
                        endABYanalizeVal = getFunc.getFieldTextByXpath(driver, "//div[4]/div/div[2]/div[1]/p[2]");
                        endXYZanalizeVal = getFunc.getFieldTextByXpath(driver, "//div[4]/div/div[2]/div[2]/p[2]");

                        if(startABYanalizeVal.equalsIgnoreCase(endABYanalizeVal) && startGroupVal.
                                equalsIgnoreCase(endGroupVal) && startXYZanalizeVal.equalsIgnoreCase(endXYZanalizeVal)){
                            throw new RuntimeException("Не відбувається змін показників інфоблоку Група");
                        }
                    }
                    for(int j = 2; j > 0; j--){
                        endGroupVal = getFunc.getFieldTextByXpath(driver, "//div/div[4]/div/div[1]");
                        endABYanalizeVal = getFunc.getFieldTextByXpath(driver, "//div[4]/div/div[2]/div[1]/p[2]");
                        endXYZanalizeVal = getFunc.getFieldTextByXpath(driver, "//div[4]/div/div[2]/div[2]/p[2]");
                        getFunc.clickSelectorByXpath(driver,"//div/div[2]/button",
                                "не можливо натиснути кнопку перехід до наступного артикула (<)");
                        if(startABYanalizeVal.equalsIgnoreCase(endABYanalizeVal) && startGroupVal.
                                equalsIgnoreCase(endGroupVal) && startXYZanalizeVal.equalsIgnoreCase(endXYZanalizeVal) ){
                            throw new RuntimeException("Не відбувається змін показників інфоблоку Група");
                        }
                    }
                    return;
                }
                case 4: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.waitForElementByXpath("//section[1]/div/div[1]/div", 10, driver);

                    getFunc.clickSelectorByXpath(driver,"//div/div/div[3]",
                            "Не можливо відкрити POP UP Menu");
                    getFunc.waitForElementByXpath("//div/div/div/div/div", 10, driver);

                    getFunc.inputMultiArticles(driver, inputValues[i]);

                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");

                    Thread.sleep(1500);
                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");

                    /** Оголошення змінних для отримання значень Групи - початкові значення до зміни артикула */
                    String startGroupVal, startABYanalizeVal, startXYZanalizeVal;
                    /** Оголошення змінних для отримання значень Групи - початкові значення до зміни артикула */
                    String endGroupVal, endABYanalizeVal,  endXYZanalizeVal;

                    startGroupVal = getFunc.getFieldTextByXpath(driver, "//div/div[4]/div/div[1]");
                    startABYanalizeVal = getFunc.getFieldTextByXpath(driver, "//div[4]/div/div[2]/div[1]/p[2]");
                    startXYZanalizeVal = getFunc.getFieldTextByXpath(driver, "//div[4]/div/div[2]/div[2]/p[2]");

                    getFunc.clickSelectorByXpath(driver,"//div/div[5]/button/i",
                            "не можливо натиснути кнопку перехід до наступного артикула (>>)");
                    endGroupVal = getFunc.getFieldTextByXpath(driver, "//div/div[4]/div/div[1]");
                    endABYanalizeVal = getFunc.getFieldTextByXpath(driver, "//div[4]/div/div[2]/div[1]/p[2]");
                    endXYZanalizeVal = getFunc.getFieldTextByXpath(driver, "//div[4]/div/div[2]/div[2]/p[2]");

                    if(startABYanalizeVal.equalsIgnoreCase(endABYanalizeVal) && startGroupVal.
                            equalsIgnoreCase(endGroupVal) && startXYZanalizeVal.equalsIgnoreCase(endXYZanalizeVal)){
                        throw new RuntimeException("Не відбувається змін показників інфоблоку Група");
                    }

                    getFunc.clickSelectorByXpath(driver,"//div[2]/div/div/div[1]/button/i",
                            "не можливо натиснути кнопку перехід до наступного артикула (<<)");
                    endGroupVal = getFunc.getFieldTextByXpath(driver, "//div/div[4]/div/div[1]");
                    endABYanalizeVal = getFunc.getFieldTextByXpath(driver, "//div[4]/div/div[2]/div[1]/p[2]");
                    endXYZanalizeVal = getFunc.getFieldTextByXpath(driver, "//div[4]/div/div[2]/div[2]/p[2]");

                    if(!(startABYanalizeVal.equalsIgnoreCase(endABYanalizeVal) && startGroupVal.
                            equalsIgnoreCase(endGroupVal) && startXYZanalizeVal.equalsIgnoreCase(endXYZanalizeVal))){
                        throw new RuntimeException("Не відбувається змін показників інфоблоку Група");
                    }
                }
                case 5:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[3]",
                            "Не можливо знайти інфоблок - Похибка");

                    getFunc.checkForDeskTopClose(driver,"//section[2]/div/div");
                    break;
                }
                case 6: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.checkForInfoBlockEnabled(driver);

                    getFunc.clickSelectorByXpath(driver,"//div/div/div[3]",
                            "Не можливо відкрити POP UP Menu");

                    getFunc.waitForElementByXpath("//div[3]/button[1]", 8, driver);

                    getFunc.clearPopUp(driver);

                    if(driver.findElements(By.cssSelector(".desktop.ng-scope")).isEmpty()){
                        throw new RuntimeException("Інфоблок Група відображається після очистки");
                    }
                    break;
                }
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "сортування ариткулів по номеру", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void V_sortArticulesByNumberTest_21() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        String[] inputValues = {"", "1158", "35124,18559,24135,18791,35813,24129,29287,35819,35828",
                "35124,18559,24135,18791,35813,24129,29287,35819,35828","11111"};
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 0: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
                    Thread.sleep(4500);

                    if(getFunc.ifElementExist("//div/span[1]/span", "Сортування по артикулу", driver))
                    {
                        throw new RuntimeException("Відображається кнопка сортування по артикулам без наявності артикулів");
                    }
                    break;
                }
                case 1: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);

                    getFunc.sendKeysByXpath(driver,"//input",
                            "Не можливо ввести артикул в поле артикула", inputValues[i]);

                    getFunc.clickSelectorByXpath(driver, "//button[2]",
                            "Не можливо натиснути кнопку (Відібрати) POP UP Меню");

                    getFunc.waitForElementByXpath("//form/div[6]/button[2]", 10, driver);
                    getFunc.isElementDisabledAlertByXpath("//form/div[6]/button[2]",driver);

                    if(!getFunc.ifElementExist("//div/span[1]/span", "Сортування по артикулу", driver))
                    {
                        throw new RuntimeException("Не відображається кнопка сортування по артикулам");
                    }
                    break;
                }
                case 2: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
                    getFunc.inputMultiArticles(driver, inputValues[i]);

                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");
                    getFunc.waitForElementByXpath("//ul/li[1]/span[1]", 10, driver);

                    /** Список відсортованих артикулів в прямому порядку (сортування здійснюється функцією)*/
                    List<Integer> sortList = getFunc.checkSortArticulesByCss("body > div:nth-child(3) > div > " +
                            "form > div.box.wrap-player > div > ul > li > span:nth-child(1)", driver);

                    getFunc.clickSelectorByXpath(driver, "//div/span[1]/span",
                            "Не можливо здійснити сортування по артикулам");

                    /** Список відсортованих артикулів в прямому порядку - інтові значення(сортування здійснюється на сторінці)*/
                    List<Integer> sortListFromPage;

                    /** Список артикулів зі сторінки - веб елементи*/
                    List<WebElement> tableEl;
                    try {
                        tableEl = driver.findElements(By.cssSelector("body > div:nth-child(3) > div > " +
                                "form > div.box.wrap-player > div > ul > li > span:nth-child(1)"));
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо отримати елементи таблиці артикулів");
                    }
                    sortListFromPage = tableEl.stream().map(e -> Integer.parseInt(e.getText())).collect(Collectors.toList());


                    if(!sortList.equals(sortListFromPage)) {
                        throw new RuntimeException("Пряме сортування відбулось не коректно");
                    }
                    break;
                }
                case 3: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
                    getFunc.inputMultiArticles(driver, inputValues[i]);

                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");
                    getFunc.waitForElementByXpath("//ul/li[1]/span[1]", 10, driver);

                    List<Integer> sortList = getFunc.checkReveceSortArticulesByCss("body > div:nth-child(3) > div > " +
                            "form > div.box.wrap-player > div > ul > li > span:nth-child(1)", driver);

                    getFunc.clickSelectorByXpath(driver, "//div/span[1]/span",
                            "Не можливо здійснити сортування по артикулам");
                    Thread.sleep(1500);

                    getFunc.clickSelectorByXpath(driver, "//div/span[1]/span",
                            "Не можливо здійснити сортування по артикулам");

                    /** Список відсортованих артикулів в прямому порядку - інтові значення(сортування здійснюється на сторінці)*/
                    List<Integer> sortListFromPage;

                    /** Список артикулів зі сторінки - веб елементи*/
                    List<WebElement> tableEl;
                    try {
                        tableEl = driver.findElements(By.cssSelector("body > div:nth-child(3) > div > " +
                                "form > div.box.wrap-player > div > ul > li > span:nth-child(1)"));
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо отримати елементи таблиці артикулів");
                    }
                    sortListFromPage = tableEl.stream().map(e -> Integer.parseInt(e.getText())).collect(Collectors.toList());

                    if(!sortList.equals(sortListFromPage)) {
                        throw new RuntimeException("Зворотне сортування відбулось не коректно");
                    }
                    break;
                }

                case 4: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);

                    getFunc.sendKeysByXpath(driver,"//input",
                            "Не можливо ввести артикул в поле артикула", inputValues[i]);

                    getFunc.clickSelectorByXpath(driver, "//button[2]",
                            "Не можливо натиснути кнопку (Відібрати) POP UP Меню");

                    getFunc.waitForElementByXpath("//form/div[4]/div/p", 10, driver);
                    getFunc.pageCheckForValue("Даних не знайдено !", driver);

                    if(getFunc.ifElementExist("//div/span[1]/span", "Сортування по артикулу", driver))
                    {
                        throw new RuntimeException("Відображається кнопка сортування по артикулам без наявності артикулів");
                    }
                    break;
                }
            }
        }

    }


    @Test(testName = "відкриття/закриття робочого столу \"прогноз\"", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void W_openCloseForecastDesktopTest_22() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        String inputValue = "316";
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 0: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValue, driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");
                    getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");
                    getFunc.checkForDeskTopClose(driver, "//section[2]/div/div");
                    break;
                }
                case 1: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValue, driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");

                    getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.clickSelectorByXpath(driver,"//span/i",
                            "Не можливо натиснути на кнопку - Х закриття робочого столу");
                    getFunc.checkForDeskTopClose(driver, "//section[2]/div/div");
                    break;
                }
                case 2: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValue, driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");
                    getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Акції)");

                    getFunc.waitForElementByXpath("//section[2]/div/div[2]", 10, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div[2]");

                    getFunc.waitForElementByXpath("//section[2]/div/div", 10, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");
                    break;
                }
                case 3: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValue, driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");
                    getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Акції)");

                    getFunc.waitForElementByXpath("//section[2]/div/div[2]", 20, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div[2]");

                    getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.clickSelectorByXpath(driver,"//div[2]/div/span/i",
                            "Не можливо натиснути на кнопку - Х закриття робочого столу (Акції)");
                    getFunc.checkForDeskTopClose(driver, "//section[2]/div/div[2]");

                    break;
                }
                case 4: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValue, driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");
                    getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.clickSelectorByXpath(driver,"//div/div/div[3]",
                            "Не можливо відкрити POP UP Menu");
                    Thread.sleep(1000);

                    getFunc.clearPopUp(driver);

                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");
                    Thread.sleep(1000);
                    getFunc.checkForDeskTopClose(driver, "//section[2]/div/div");
                    break;
                }
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "відкриття/закриття робочого столу \"Акції\"", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void X_openCloseSharesDesktopTest_23() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        String inputValue = "316";
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 0: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValue, driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Акції)");
                    getFunc.waitForElementByXpath("//section[2]/div/div", 15, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Акції)");
                    getFunc.checkForDeskTopClose(driver, "//section[2]/div/div");
                    break;
                }
                case 1: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValue, driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Акції)");

                    getFunc.waitForElementByXpath("//section[2]/div/div", 15, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.clickSelectorByXpath(driver,"//span/i",
                            "Не можливо натиснути на кнопку - Х закриття робочого столу");
                    getFunc.checkForDeskTopClose(driver, "//section[2]/div/div");
                    break;
                }
                case 2: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValue, driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Акції)");
                    getFunc.waitForElementByXpath("//section[2]/div/div", 15, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Прогноз)");

                    getFunc.waitForElementByXpath("//section[2]/div/div[2]", 15, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div[2]");

                    getFunc.waitForElementByXpath("//section[2]/div/div", 15, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");
                    break;
                }
                case 3: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValue, driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Акції)");
                    getFunc.waitForElementByXpath("//section[2]/div/div", 15, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");

                    getFunc.waitForElementByXpath("//section[2]/div/div[2]", 15, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div[2]");

                    getFunc.waitForElementByXpath("//section[2]/div/div", 15, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.clickSelectorByXpath(driver,"//div[2]/div/span/i",
                            "Не можливо натиснути на кнопку - Х закриття робочого столу (ПРОГНОЗ)");
                    getFunc.checkForDeskTopClose(driver, "//section[2]/div/div[2]");

                    break;
                }
                case 4: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValue, driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Акції)");
                    getFunc.waitForElementByXpath("//section[2]/div/div", 15, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.clickSelectorByXpath(driver,"//div/div/div[3]",
                            "Не можливо відкрити POP UP Menu");
                    Thread.sleep(1000);

                    getFunc.clearPopUp(driver);

                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");
                    Thread.sleep(1000);
                    getFunc.checkForDeskTopClose(driver, "//section[2]/div/div");
                    break;
                }
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "відкриття/закриття робочого столу \"Графіки\"", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void YA_openCloseGraphicsDesktopTest_24() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        String inputValue = "609642";
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 0: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValue, driver);

                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Графіки)");
                    getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");
                    break;
                }
                case 2: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValue, driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[5]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Графіки)");
                    getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.waitForElementByXpath("//div[2]/div/span/i", 20, driver);
                    Thread.sleep(5000);
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div/span/i",
                            "Не можливо натиснути на кнопку - Х закриття робочого столу (Графіки)");
                    getFunc.checkForDeskTopClose(driver, "//section[2]/div/div[2]");
                    break;
                }
                case 3: {
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValue, driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[5]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Графіки)");
                    getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[5]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Графіки)");
                    getFunc.checkForDeskTopClose(driver, "//section[2]/div/div[2]");

                    Thread.sleep(1500);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[5]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Графіки)");
                    getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");
                    break;
                }
                case 4: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValue, driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[5]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Графіки)");
                    getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.waitForElementByXpath("//div/div/div[3]", 15, driver);
                    getFunc.clickSelectorByXpath(driver,"//div/div/div[3]",
                            "Не можливо відкрити POP UP Menu");
                    Thread.sleep(1000);

                    getFunc.clearPopUp(driver);

                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");
                    Thread.sleep(1000);
                    getFunc.checkForDeskTopClose(driver, "//section[2]/div/div");
                }
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "відкриття/закриття робочого столу \"Звіти\"", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void Y_openCloseReportsDesktopTest_25() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        String[] inputValues = {"1158", "125311", "1158", "1138", "125036, 125037, 125038, 125039, 125040, 125041",
                "125036, 125037, 125038, 125039, 125040, 125041", "1158", "832658"};
        int arrayLength = inputValues.length;

        for (int i = 0; i < arrayLength; i++) {
            switch (i) {
                case 0: {
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[6]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[6]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Звіти)");

                    getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    Thread.sleep(5000);
                    getFunc.waitForElementByXpath("//div[1]/span/i", 15, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[1]/span/i",
                            "Не можливо натиснути на кнопку - Х закриття робочого столу (Звіти)");

                    getFunc.checkForDeskTopClose(driver, "//section[2]/div/div");
                    break;
                }

                case 1:{
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[6]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[6]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Звіти)");

                    getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div");

                    getFunc.waitForElementByXpath("//section[1]/div/div[1]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Прогноз)");

                    getFunc.waitForElementByXpath("//section[2]/div/div[1]", 15, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div[1]");

                    getFunc.waitForElementByXpath("//section[2]/div/div[1]", 15, driver);
                    getFunc.checkForDeskTopOpen(driver, "//section[2]/div/div[1]");

                    getFunc.checkWorkDesktopAttribute(driver, "block-forecast");

                    getFunc.checkWorkDesktopAttribute(driver, "block-reports");

                    getFunc.waitForElementByXpath("//section[1]/div/div[1]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Акції)");

                    getFunc.checkWorkDesktopAttribute(driver, "block-shares");
                break;
                }

                case 2:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[6]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[6]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Звіти)");

                    getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    getFunc.checkForDeskTopOpen(driver,"//section[2]/div/div");

                    getFunc.waitForElementByXpath("//section[1]/div/div[6]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[6]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Звіти)");

                    getFunc.checkForDeskTopClose(driver,"//section[2]/div/div");
                break;
                }

                case 3:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[6]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[6]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Звіти)");

                    getFunc.waitForElementByXpath("//div/div/div[3]", 15, driver);
                    getFunc.getSearchArticle("1158", driver);

                    Thread.sleep(1500);
                    //getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    getFunc.checkForDeskTopClose(driver, "//section[2]/div/div");
                break;
                }

                case 4:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);

                    getFunc.waitForElementByXpath("//div/div/div/div/div", 20, driver);
                    getFunc.inputMultiArticles(driver,inputValues[i]);

                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");

                    getFunc.waitForElementByXpath("//div/ul/li/span[2]", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");

                   getFunc.waitForElementByXpath("//section[1]/div/div[6]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[6]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Звіти)");

                    /** Змінна для початкового значення артикула*/
                    String articleBegin;
                    /** Змінна для кінцевого значення артикула*/
                    String articleEnd = "" ;

                    try {
                        articleBegin = driver.findElement(By.xpath("//div/div/div[3]")).getText();
                    }catch (Exception e){
                        throw new RuntimeException("Не можливо знайти елемент артикул POP UP Menu");
                    }

                    for(int j = 0; j < 3; j++){
                        getFunc.waitForElementByXpath("//div[4]/button/i", 20, driver);
                        getFunc.clickSelectorByXpath(driver,"//div[4]/button/i",
                                "не можливо натиснути кнопку перехід до наступного артикула (>)");
                        try {
                            articleEnd = driver.findElement(By.xpath("//div/div/div[3]")). getText();
                        }catch (Exception e){
                            throw new RuntimeException("Не можливо знайти елемент артикул POP UP Menu");
                        }
                    }

                    if (!articleBegin.equals(articleEnd)){
                        for(int j = 0; j < 3; j++){
                            getFunc.waitForElementByXpath("//div/div[2]/button", 10, driver);
                            getFunc.clickSelectorByXpath(driver,"//div/div[2]/button",
                                    "не можливо натиснути кнопку перехід до наступного артикула (<)");
                        }
                        try {
                            articleEnd = driver.findElement(By.xpath("//div/div/div[3]")).getText();
                        }catch (Exception e){
                            throw new RuntimeException("Не можливо знайти елемент артикул POP UP Menu");
                        }
                    }
                    else {
                        throw new RuntimeException("Не відбулась зміна артикулів");
                    }

                    if(!articleBegin.equals(articleEnd)){
                        throw new RuntimeException("Не відбулося переключення до початкового артикула");
                    }
                break;
                }

                case 5:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);

                    getFunc.waitForElementByXpath("//div/div/div/div/div", 10, driver);
                    getFunc.inputMultiArticles(driver,inputValues[i]);

                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");

                    getFunc.waitForElementByXpath("//div/ul/li/span[2]", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");

                    getFunc.waitForElementByXpath("//section[1]/div/div[6]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[6]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Звіти)");

                    /** Змінна для початкового значення артикула*/
                    String articleBegin;
                    /** Змінна для кінцевого значення артикула*/
                    String articleEnd = "0" ;

                    try {
                        articleBegin = driver.findElement(By.xpath("//div/div/div[3]")).getText();
                    }catch (Exception e){
                        throw new RuntimeException("Не можливо знайти елемент артикул POP UP Menu");
                    }

                    getFunc.waitForElementByXpath("//div[4]/button/i", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div/div[5]/button/i",
                            "не можливо натиснути кнопку перехід до наступного артикула (>>)");
                    try {
                        articleEnd = driver.findElement(By.xpath("//div/div/div[3]")). getText();
                    }catch (Exception e){
                        throw new RuntimeException("Не можливо знайти елемент артикул POP UP Menu");
                    }

                    if (!articleBegin.equals(articleEnd)){
                        getFunc.waitForElementByXpath("//div/div[2]/button", 10, driver);
                        getFunc.clickSelectorByXpath(driver,"//div[2]/div/div/div[1]/button/i",
                                "не можливо натиснути кнопку перехід до наступного артикула (<<)");

                        try {
                            articleEnd = driver.findElement(By.xpath("//div/div/div[3]")).getText();
                        }catch (Exception e){
                            throw new RuntimeException("Не можливо знайти елемент артикул POP UP Menu");
                        }
                    }
                    else {
                        throw new RuntimeException("Не відбулась зміна артикулів");
                    }

                    if(!articleBegin.equals(articleEnd)){
                        throw new RuntimeException("Не відбулося переключення до початкового артикула");
                    }
                break;
                }

                case 6: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);

                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[6]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[6]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Звіти)");

                    getFunc.waitForElementByXpath("//div/div/div[3]", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div/div/div[3]",
                            "Не можливо відкрити POP UP Menu");

                    getFunc.waitForElementByXpath("//div[3]/button[1]", 10, driver);
                    getFunc.clearPopUp(driver);

                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");

                    getFunc.checkForDeskTopClose(driver, "//section[2]/div/div");
                    break;
                }
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "відображеня інформації у робочому столі \"акції\"", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void ZA_showSharesInformationDesktopTest_26() throws Exception {
//        startBrowser();

        getFunc.catchStatusLoadPage();

        String[] inputValues = {"648877", "53599", "53599", "1158, 53599, 136", "53599, 136"};
        String [] sharesValue = {"", "Торец 2016 (41)(24.11-07.12.16)",
                "Торец 2016 (41)(24.11-07.12.16)", "Торец 2016 (41)(24.11-07.12.16)"};


        int arrayLength = inputValues.length;
        for (int i = 0; i < arrayLength; i++) {
            switch (i) {
                case 0: {
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[6]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Акції)");
                    WebElement shareName;
                    try {
                        shareName = driver.findElement(By.xpath("//tbody/tr/td"));
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо звернутись до робочого столу Акції");
                    }

                    if(!shareName.getText().equalsIgnoreCase("Даних немає")){
                        throw new RuntimeException("В робочому столі Акції відсутня інформація Даних не знайдено");
                    }
                    break;
                }
                case 1:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[6]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Акції)");
                    WebElement shareName;
                    try {
                        shareName = driver.findElement(By.xpath("//tbody/tr/td[1]"));
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо звернутись до робочого столу Акції");
                    }

                    if(!shareName.getText().equalsIgnoreCase(sharesValue[i])){
                        throw new RuntimeException("В робочому столі Акції відсутня інформація, Даних не знайдено");
                    }
                    break;
                }
                case 2:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[6]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Акції)");
                    WebElement shareName;

                    try {
                        shareName = driver.findElement(By.xpath("//tbody/tr/td[1]"));
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо звернутись до робочого столу Акції");
                    }

                    if(!shareName.getText().equalsIgnoreCase(sharesValue[i])){
                        throw new RuntimeException(String.format("В робочому столі Акції відсутня інформація %s",
                                sharesValue[i]));
                    }

                    try {
                        shareName = driver.findElement(By.xpath("//table/tbody/tr/td[1]"));
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо звернутись до робочого столу Акції");
                    }

                    if(!shareName.getText().equalsIgnoreCase(sharesValue[i + 1])){
                        throw new RuntimeException(String.format("В робочому столі Акції відсутня інформація %s",
                                sharesValue[i + 1]));
                    }
                    break;
                }
                case 3:{
                    String [] sharesValues = {"Даних немає", "(Ф) Газета №23 (30.11-13.12.16)",
                            "Торец 2016 (41)(24.11-07.12.16)"};
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);

                    getFunc.waitForElementByXpath("//div/div/div/div/div", 10, driver);
                    getFunc.inputMultiArticles(driver,inputValues[i]);

                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");

                    getFunc.waitForElementByXpath("//div/ul/li/span[2]", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");

                    getFunc.waitForElementByXpath("//section[1]/div/div[2]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Акції)");

                    WebElement shareName;

                    for(int j = 0; j < 3; j++) {

                        getFunc.waitForElementByXpath("//table/tbody/tr/td[1]", 10, driver);

                        try {
                            shareName = driver.findElement(By.xpath("//table/tbody/tr/td[1]"));
                        } catch (Exception e){
                            throw new RuntimeException("Не можливо звернутись до робочого столу Акції");
                        }

                        if(!shareName.getText().equalsIgnoreCase(sharesValues[j])){
                        throw new RuntimeException(String.format("В робочому столі Акції відсутня інформація %s",
                                sharesValues[j]));
                        }

                        getFunc.waitForElementByXpath("//div[4]/button/i", 10, driver);
                        getFunc.clickSelectorByXpath(driver,"//div[4]/button/i",
                                "не можливо натиснути кнопку перехід до наступного артикула (>)");
                    }
                    break;
                }

                case 4: {
                    String [] sharesValues = {"Даних немає", "Торец 2016 (41)(24.11-07.12.16)"};
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);

                    getFunc.waitForElementByXpath("//div/div/div/div/div", 10, driver);
                    getFunc.inputMultiArticles(driver,inputValues[i]);

                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");

                    getFunc.waitForElementByXpath("//div/ul/li/span[2]", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");

                    getFunc.waitForElementByXpath("//section[1]/div/div[2]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Акції)");

                    WebElement shareName;

                    for(int j = 0; j < 2; j++) {

                        getFunc.waitForElementByXpath("//table/tbody/tr/td[1]", 10, driver);

                        try {
                            shareName = driver.findElement(By.xpath("//table/tbody/tr/td[1]"));
                        } catch (Exception e){
                            throw new RuntimeException("Не можливо звернутись до робочого столу Акції");
                        }

                        if(!shareName.getText().equalsIgnoreCase(sharesValues[j])){
                            throw new RuntimeException(String.format("В робочому столі Акції відсутня інформація %s",
                                    sharesValues[j]));
                        }

                        getFunc.waitForElementByXpath("//div/div[5]/button/i", 10, driver);
                        getFunc.clickSelectorByXpath(driver,"//div/div[5]/button/i",
                                "не можливо натиснути кнопку перехід до наступного артикула (>>)");
                    }
                    break;
                }
            }
        }
        System.gc();
//        stopBrowser();
    }



    @Test(testName = "відображеня інформації у робочому столі \"прогноз\"", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void ZB_showForecastInformationDesktopTest_27() throws Exception {
        startBrowser();
        getFunc.catchStatusLoadPage();

        String[] inputValues = {"1142", "1168", "136", "1158, 1168, 1169, 1171"};

        int arrayLength = inputValues.length;
        for (int i = 0; i < arrayLength; i++) {
            switch (i) {
                case 0: {
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[1]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");
                    getFunc.verifyForecastDesktop(driver);
                    break;
                }
                case 1: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//div[2]/div[2]/span/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[2]/span/button",
                            "Не можливо здійснити клік на кнопку (Дата закінчення періоду)");

                    getFunc.clickSelectorByXpath(driver, "//tr/th[3]/button",
                            "Не можливо натиснути ккнопку переключення до наступного року");

                    getFunc.waitForElementByXpath(monthXpath[9], 10, driver);
                    getFunc.isElementDisabledAlertByXpath(monthXpath[9], driver);

                    getFunc.clickSelectorByXpath(driver,
                            (monthXpath[9]), String.format("Не можливо здійснити вибір місяця: %s",
                                    monthName[9]));

                    getFunc.waitForElementByXpath("//month-picker/div/div[4]/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//month-picker/div/div[4]/button",
                            "неможливо натиснути кнопку панелі фільтрів: (Застосувати дати)");

                    getFunc.waitForElementByXpath("//section[1]/div/div[1]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");

                    getFunc.checkSumForecastDeskTop(driver);
                    break;
                }
                case 2: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[1]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");

                    /** Вебелемент прогноз*/
                    WebElement forecastEl;
                    /** Вебелемент робочий стіл*/
                    WebElement deskTopEl;

                    try{
                        forecastEl = driver.findElement(By.xpath("//section[1]/div/div[1]/div/div"));
                    } catch (Exception e) {
                        throw new RuntimeException("Не можливо отримати величину прогнозу з інфоблоку");
                    }

                    try {
                        deskTopEl = driver.findElement(By.xpath("//table/tbody/tr/td"));
                    } catch (Exception e) {
                        throw new RuntimeException("Не можливо отримати величину прогнозу з робочого столу");
                    }

                    if(!forecastEl.getText().equals(deskTopEl.getText())){
                        throw new RuntimeException("Прогноз інфоблоку не відповідає прогнозу робочого столу");
                    }
                    break;
                }
                case 3: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);

                    getFunc.waitForElementByXpath("//div/div/div/div/div", 10, driver);
                    getFunc.inputMultiArticles(driver,inputValues[i]);

                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");

                    getFunc.waitForElementByXpath("//div/ul/li/span[2]", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");

                    getFunc.waitForElementByXpath("//div[2]/div[2]/span/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[2]/span/button",
                            "Не можливо здійснити клік на кнопку (Дата закінчення періоду)");

                    getFunc.waitForElementByXpath("//tr/th[3]/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//tr/th[3]/button",
                            "Не можливо натиснути ккнопку переключення до наступного року");

                    getFunc.waitForElementByXpath(monthXpath[9], 10, driver);
                    getFunc.isElementDisabledAlertByXpath(monthXpath[9], driver);

                    getFunc.clickSelectorByXpath(driver,
                            (monthXpath[9]), String.format("Не можливо здійснити вибір місяця: %s",
                                    monthName[9]));

                    getFunc.waitForElementByXpath("//month-picker/div/div[4]/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//month-picker/div/div[4]/button",
                            "неможливо натиснути кнопку панелі фільтрів: (Застосувати дати)");

                    getFunc.waitForElementByXpath("//section[1]/div/div[1]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");

                    getFunc.checkSumForecastDeskTop(driver);
                    break;
                }
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Елемент період графіка, дата початку", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void ZC_startDateGraphicsDesktopTest_28() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        DateFormat dateFormat = new SimpleDateFormat("MM");
        Date date = new Date();

        String[] inputValues = {"1158", "1158", "1158", "1158", "1158"};

        int arrayLength = inputValues.length;
        for (int i = 0; i < arrayLength; i++) {
            switch (i) {
                case 0: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValues[i], driver);
                    String startDescTopDate;

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.waitForElementByXpath("//div/div[1]/div/div/div[1]/div[1]/input", 10, driver);
                    try {
                        /** Початкове значення дати  початку з робочого столу*/
                        WebElement datePickerStartValue = driver.findElement(By.xpath("//div/div[1]/div/div/div[1]/div[1]/input"));
                        startDescTopDate = datePickerStartValue.getAttribute("value");
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо здійснити зчитування дати початку періоду ");
                    }

                    String [] startDescTopDateArr = startDescTopDate.split(" ");
                    int monthCheckVal = Integer.parseInt(dateFormat.format(date)) - 2;

                    if(monthCheckVal == -1){
                        monthCheckVal = 11;
                    }

                    if(!startDescTopDateArr[0].equals(monthName[monthCheckVal])){
                        throw new RuntimeException("Невірно відображається початок періоду в робочому столі графіки");
                    }
                    break;
                }
                case 1:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValues[i], driver);

                    /** корегування значення місяця для отримання шляху з масиву*/
                    int monthCorrectionFirst = 2;
                    /** Значення місяця з урахуванням корегування*/
                    int monthCheckVal = Integer.parseInt(dateFormat.format(date)) - monthCorrectionFirst;

                    /** Перевірка чи під час корегування місяць не зайшов в мінус */
                    if(monthCheckVal == -1){
                        monthCheckVal = 11;
                    }

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    Thread.sleep(5000);
                    getFunc.waitForElementByXpath("//div/div[1]/div[2]/span/i", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//div/div[1]/div[2]/span/i",
                            "не можливо натиснути кнопку вибору дати початку періоду робочого столу Графіки");

                    for(int j =0 ; j < 2; j++){
                        getFunc.waitForElementByXpath("//th[1]/button", 10, driver);
                        getFunc.clickSelectorByXpath(driver, "//th[1]/button",
                                "Не можливо натиснути кнопку переключення до попереднього місяця < (дата початку періоду)");
                    }

                    /** корегування значення місяця для отримання шляху з масиву*/
                    int monthCorrection = 1;

                    for (int m = monthCheckVal - monthCorrection; m >= 0; m--) {
                        getFunc.isElementEnabledAlertByXpath((monthXpath[m]), driver);
                    }

                    getFunc.clickSelectorByXpath(driver, monthXpath[monthCheckVal],
                            "Не можливо вибрати значення місяця");

                    getFunc.clickSelectorByXpath(driver, "//div[1]/div[1]/div/div[2]/button",
                            "Не можливо вибрати значення місяця");
                    break;
                }
                case 2:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValues[i], driver);

                    /** корегування значення місяця для отримання шляху з масиву*/
                    int monthCorrectionFirst = 2;
                    /** Значення місяця з урахуванням корегування*/
                    int monthCheckVal = Integer.parseInt(dateFormat.format(date)) - monthCorrectionFirst;

                    /** Перевірка чи під час корегування місяць не зайшов в мінус */
                    if(monthCheckVal == -1){
                        monthCheckVal = 11;
                    }

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    Thread.sleep(5000);
                    getFunc.waitForElementByXpath("//div/div[1]/div[2]/span/i", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//div/div[1]/div[2]/span/i",
                            "не можливо натиснути кнопку вибору дати початку періоду робочого столу Графіки");

                    getFunc.waitForElementByXpath("//th[3]/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//th[3]/button",
                            "Не можливо натиснути кнопку переключення до наступного місяця > (дата початку періоду)");

                    if (Integer.parseInt(dateFormat.format(date)) < 12) {
                        //pozje potochnogo mesjaca
                        for (int m = (monthCheckVal + 2); m < (monthXpath.length - 1); m++) {
                            getFunc.isElementEnabledAlertByXpath((monthXpath[m]), driver);
                        }
                    }
                    break;
                }
                case 3: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValues[i], driver);

                    /** корегування значення місяця для отримання шляху з масиву*/
                    int monthCorrectionFirst = 2;
                    /** Значення місяця з урахуванням корегування*/
                    int monthCheckVal = Integer.parseInt(dateFormat.format(date)) - monthCorrectionFirst;

                    /** Перевірка чи під час корегування місяць не зайшов в мінус */
                    if(monthCheckVal == -1){
                        monthCheckVal = 11;
                    }

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.waitForElementByXpath("//div/div[1]/div[2]/span/i", 20, driver);

                    getFunc.clickSelectorByXpath(driver, "//div/div[1]/div[2]/span/i",
                            "не можливо натиснути кнопку вибору дати початку періоду робочого столу Графіки");

                    getFunc.waitForElementByXpath("//th[3]/button", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//th[3]/button",
                            "Не можливо натиснути кнопку переключення до наступного місяця > (дата початку періоду)");
                    if(monthCheckVal != 0){
                        for (int k = 0; k < (monthCheckVal + 1); k++) {
                            //getFunc.waitForElementByXpath(monthXpath[k], driver);
                            getFunc.isElementDisabledAlertByXpath(monthXpath[k], driver);
                        }
                    }

                    for(int p = 0; p < 2; p++){
                        getFunc.waitForElementByXpath("//th[1]/button", 20, driver);
                        getFunc.clickSelectorByXpath(driver, "//th[1]/button",
                                "Не можливо натиснути кнопку переключення до попереднього місяця < (дата початку періоду)");
                        Thread.sleep(1000);
                        for(int l = 0; l < 12; l++){
                            getFunc.isElementDisabledAlertByXpath(monthXpath[l], driver);
                        }
                    }

                    getFunc.clickSelectorByXpath(driver, "//th[1]/button",
                            "Не можливо натиснути кнопку переключення до попереднього місяця < (дата початку періоду)");
                    for (int s = 0; s < monthCheckVal; s++){
                        getFunc.isElementEnabledAlertByXpath((monthXpath[s]), driver);
                    }
                    break;
                }
                case 4: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div",20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.waitForElementByXpath("//div/div[1]/div[2]/span/i", 20, driver);
                    getFunc.clickSelectorByXpath(driver, "//div/div[1]/div[2]/span/i",
                            "не можливо натиснути кнопку вибору дати початку періоду робочого столу Графіки");

                    Thread.sleep(1500);
                    getFunc.clickSelectorByXpath(driver, "//th[1]/button",
                            "Не можливо натиснути кнопку переключення до попереднього місяця < (дата початку періоду)");

                    Thread.sleep(1500);
                    getFunc.clickSelectorByXpath(driver, "//tr[2]/td[3]/button",
                            "Не можливо Вибрати червень місяць як дату початку");

                    Thread.sleep(1500);
                    getFunc.clickSelectorByXpath(driver, "//section[2]/div/div/div/div[1]/div[1]/div/div[2]/button",
                            "неможливо натиснути кнопку робочого столу: (Застосувати дати)");

                    /** Вебелемент початкової дати початкове значення*/
                    WebElement startCondition;
                    /** Вебелемент початкової дати кінцеве значення*/
                    WebElement endCondition;

                    /** Текстове значення дати - початкове значення*/
                    String start;
                    try {

                        getFunc.waitForElementByXpath("//div[1]/div/div/div[1]/div[1]/input", 20, driver);
                        startCondition = driver.findElement(By.xpath("//div[1]/div/div/div[1]/div[1]/input"));
                        start = startCondition.getAttribute("value");
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо зафіксувати стан початкової дати");
                    }

                    getFunc.clickSelectorByXpath(driver,"//div[1]/span/i",
                            "Не можливо натиснути на кнопку - Х закриття робочого столу");

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    /** Текстове значення дати - кінцеве значення*/
                    String end;
                    try {
                        getFunc.waitForElementByXpath("//div[1]/div/div/div[1]/div[1]/input", 20, driver);
                        endCondition = driver.findElement(By.xpath("//div[1]/div/div/div[1]/div[1]/input"));
                        end = endCondition.getAttribute("value");
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо повторно зафіксувати стан початкової дати");
                    }

                    if(!start.equalsIgnoreCase(end)){
                        throw new RuntimeException("Початковий період після повторного відкриття " +
                                "робочого столу не відповідає попередньому значенню");
                    }
                    break;
                }
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "Елемент період графіка, дата кінця", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void ZD_endDateGraphicsDesktopTest_29() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();

        /** Об'єкти для отримання значення поточного місяця та поточного року */
        DateFormat dateFormat = new SimpleDateFormat("MM");
        DateFormat currentYear = new SimpleDateFormat("yyyy");

        Date date = new Date();

        /** корегування значення місяця для отримання шляху з масиву*/
        int monthCorrectionFirst = 1;
        /** Значення місяця з урахуванням корегування*/
        int monthCheckVal = Integer.parseInt(dateFormat.format(date)) - monthCorrectionFirst;

        /** Перевірка чи під час корегування місяць не зайшов в мінус */
        if(monthCheckVal == -1){
            monthCheckVal = 11;
        }

        String[] inputValues = {"1158", "1158", "1158", "1158", "1158"};

        int arrayLength = inputValues.length;
        for (int i = 0; i < arrayLength; i++) {
            switch (i) {
                case 0: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.waitForElementByXpath("//div[1]/div/div/div[2]/div[1]/input", 20, driver);
                    try {
                        /** Вебелемент кінцевої дати робочого столу*/
                        WebElement datePickerStartValue = driver.findElement(By.xpath("//div[1]/div/div/div[2]/div[1]/input"));
                        String dateVal = datePickerStartValue.getAttribute("value");
                        if (!dateVal.equalsIgnoreCase(monthName[monthCheckVal] + " - " + currentYear.format(date))) {
                            throw new RuntimeException("Початкова дата відрізняється від заданої !");
                        }
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо здійснити зчитування дати початку періоду ");
                    }
                }
                case 1: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.waitForElementByXpath("//div/div[2]/div[2]/span/i", 20, driver);
                    getFunc.clickSelectorByXpath(driver, "//div/div[2]/div[2]/span/i",
                            "не можливо натиснути кнопку вибору дати закінчення періоду робочого столу Графіки");

                    getFunc.waitForElementByXpath("//th[3]/button", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//th[3]/button",
                            "Не можливо натиснути кнопку переключення до наступного місяця > (дата початку періоду)");

                    getFunc.clickSelectorByXpath(driver, monthXpath[monthCheckVal], String.format("не можливо вибрати місяць %s", monthXpath[monthCheckVal]));

                    getFunc.clickSelectorByXpath(driver, "//section[2]/div/div/div/div[1]/div[1]/div/div[2]/button",
                            "неможливо натиснути кнопку робочого столу: (Застосувати дати)");

                    Thread.sleep(1000);
                    break;
                }
                case  2: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.waitForElementByXpath("//div/div[2]/div[2]/span/i", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//div/div[2]/div[2]/span/i",
                            "не можливо натиснути кнопку вибору дати закінчення періоду робочого столу Графіки");

                    getFunc.waitForElementByXpath("//th[1]/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//th[1]/button",
                            "Не можливо натиснути кнопку переключення до попереднього місяця < (дата початку періоду)");

                    try{
                        getFunc.waitForElementByXpath("//th[3]/button", 10, driver);
                        getFunc.isElementEnabledAlertByXpath(monthXpath[monthCheckVal - 2], driver);
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо перевірити вибір кінцевого період раніше чим початкового");
                    }
                    break;
                }
                case 3: {
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.waitForElementByXpath("//div/div[2]/div[2]/span/i", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//div/div[2]/div[2]/span/i",
                            "не можливо натиснути кнопку вибору дати закінчення періоду робочого столу Графіки");

                    getFunc.waitForElementByXpath("//th[3]/button", 10, driver);
                    for (int j = monthCheckVal; j < 11; j++) {
                        getFunc.isElementDisabledAlertByXpath(monthXpath[monthCheckVal + 1], driver);
                    }

                    getFunc.waitForElementByXpath("//th[3]/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//th[3]/button",
                            "Не можливо натиснути кнопку переключення до наступного місяця > (дата початку періоду)");

                    getFunc.waitForElementByXpath("//th[3]/button", 10, driver);
                    for (int j = 0; j < monthCheckVal; j++) {
                        getFunc.isElementDisabledAlertByXpath(monthXpath[monthCheckVal], driver);
                    }
                    break;
                }
                case 4: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    /** Вебелемент початкової дати початкове значення*/
                    WebElement startCondition;
                    /** Вебелемент початкової дати кінцеве значення*/
                    WebElement endCondition;

                    /** Текстове значення дати - початкове значення*/
                    String start;
                    try {
                        getFunc.waitForElementByXpath("//div[1]/div/div/div[2]/div[1]/input", 10, driver);
                        startCondition = driver.findElement(By.xpath("//div[1]/div/div/div[2]/div[1]/input"));
                        start = startCondition.getAttribute("value");
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо зафіксувати стан початкової дати");
                    }

                    getFunc.waitForElementByXpath("//div/div[2]/div[2]/span/i", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//div/div[2]/div[2]/span/i",
                            "не можливо натиснути кнопку вибору дати закінчення періоду робочого столу Графіки");

                    getFunc.waitForElementByXpath("//li[2]/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//li[2]/button",
                            "Не можливо натиснути кнопку Close форми вибору періоду");

                    /** Текстове значення дати - кінцеве значення*/
                    String end;
                    try {
                        getFunc.waitForElementByXpath("//div[1]/div/div/div[2]/div[1]/input", 10, driver);
                        endCondition = driver.findElement(By.xpath("//div[1]/div/div/div[2]/div[1]/input"));
                        end = endCondition.getAttribute("value");
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо повторно зафіксувати стан початкової дати");
                    }

                    if(!start.equalsIgnoreCase(end)){
                        throw new RuntimeException("Початковий період після повторного відкриття " +
                                "робочого столу не відповідає попередньому значенню");
                    }
                    break;
                }
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "включення/виключення елементів графіка", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void ZE_onOffElementsGraphicsDesktopTest_30() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();
        String[] inputValues = {"1158", "1158", "1158", "1158", "1158", "1158", "1158"};
        int arrayLength = inputValues.length;
        for (int i = 0; i < arrayLength; i++) {
            switch (i) {
                case 0: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.waitForElementByXpath("//div[1]/div/div/div[2]/div[1]/input", 10, driver);

                    getFunc.checkBoxDisabledAlertByXpath(driver, "//*[@id=\"AllPSales\"]");
                    getFunc.checkBoxDisabledAlertByXpath(driver, "//*[@id=\"Forecast\"]");

                    getFunc.checkBoxEnabledAlertByXpath(driver, "//*[@id=\"Shares\"]");
                    getFunc.checkBoxEnabledAlertByXpath(driver, "//*[@id=\"SumSales\"]");
                    getFunc.checkBoxEnabledAlertByXpath(driver, "//*[@id=\"ModelValue\"]");
                    getFunc.checkBoxEnabledAlertByXpath(driver, "//*[@id=\"Price\"]");

                    break;
                }
                case 1: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.waitForElementByXpath("//div[1]/div/div/div[2]/div[1]/input", 20, driver);
                    getFunc.waitForElementByXpath("//div[2]/label", 20, driver);

                    getFunc.clickSelectorByXpath(driver,"//div[2]/label",
                            "Не можливо натиснути на чекбокс потенційні продажі");
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[1]/div[4]",
                            "Не можливо натиснути на чекбокс потенцыйны продажі");

                    /** Текст елемена, який виводить повідомлення на робочий стіл графіки*/
                    String getText;
                    /** Текст повідомлення*/
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
                    break;
                }
                case 2: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.waitForElementByXpath("//div[1]/div/div/div[2]/div[1]/input", 10, driver);

                    getFunc.clickSelectorByXpath(driver,"//label",
                            "Не можливо натиснути на чекбокс фактичні продажі");
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[1]/div[3]",
                            "Не можливо натиснути на чекбокс модельні значення");
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[1]/div[5]",
                            "Не можливо натиснути на чекбокс графік цін");
                    break;
                }
                case 3: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.getOffDefaultGraphics(driver);
                    getFunc.clickSelectorByXpath(driver,"//label",
                              "Не можливо натиснути на чекбокс фактичні продажі");

                    break;
                }
                case 4: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.getOffDefaultGraphics(driver);

                    getFunc.clickSelectorByXpath(driver,"//div[2]/label",
                            "Не можливо натиснути на чекбокс потенційні продажі");
                    break;
                }
                case 5: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.getOffDefaultGraphics(driver);

                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[1]/div[3]",
                            "Не можливо натиснути на чекбокс модельні значення");
                    break;
                }
                case 6: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.waitForElementByXpath("//div[1]/div/div/div[2]/div[1]/input", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//label",
                            "Не можливо натиснути на чекбокс фактичні продажі");

                    getFunc.waitForElementByXpath("//div[2]/div[1]/div[3]", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[1]/div[3]",
                            "Не можливо натиснути на чекбокс модельні значення");

                    getFunc.waitForElementByXpath("//div[2]/div[1]/div[5]", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[1]/div[5]",
                            "Не можливо натиснути на чекбокс графік цін");

                    getFunc.waitForElementByXpath("//div[2]/div[2]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[2]/div",
                            "Не можливо натиснути на чекбокс Акції");

                    getFunc.clickSelectorByXpath(driver,"//span/i",
                            "Не можливо натиснути на кнопку - Х закриття робочого столу");

                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.waitForElementByXpath("//div[1]/div/div/div[2]/div[1]/input", 10, driver);

                    /** Перевірка чекбоксів на включений стан*/
                    getFunc.checkBoxDisabledAlertByXpath(driver, "//*[@id=\"AllPSales\"]");
                    getFunc.checkBoxDisabledAlertByXpath(driver, "//*[@id=\"Forecast\"]");
                    getFunc.checkBoxDisabledAlertByXpath(driver, "//*[@id=\"Shares\"]");
                    getFunc.checkBoxDisabledAlertByXpath(driver, "//*[@id=\"SumSales\"]");
                    getFunc.checkBoxDisabledAlertByXpath(driver, "//*[@id=\"ModelValue\"]");
                    getFunc.checkBoxDisabledAlertByXpath(driver, "//*[@id=\"Price\"]");
                    break;
                }

            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "графік цін", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void ZF_priceGraphiсGraphicsDesktopTest_31() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();
        String[] inputValues = {"1158", "1882", "18791"};
        int arrayLength = inputValues.length;
        for (int i = 0; i < arrayLength; i++) {
            switch (i) {
                case 0: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.waitForElementByXpath("//div[1]/div/div/div[2]/div[1]/input", 10, driver);

                    getFunc.getOffDefaultGraphics(driver);
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[1]/div[5]",
                            "Не можливо натиснути на чекбокс графік цін");

                    getFunc.checkBoxDisabledAlertByXpath(driver, "//*[@id=\"Price\"]");
                    break;
                }
                case 1: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.waitForElementByXpath("//div[1]/div/div/div[2]/div[1]/input", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[2]/label",
                            "Не можливо натиснути на чекбокс потенційні продажі");

                    getFunc.clickSelectorByXpath(driver,"//div[1]/div[4]/label",
                            "Не можливо натиснути на чекбокс Прогноз");
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[1]/div[5]",
                            "Не можливо натиснути на чекбокс графік цін");

                    getFunc.checkBoxDisabledAlertByXpath(driver, "//*[@id=\"Price\"]");

                    /** Текст елемена, який виводить повідомлення на робочий стіл графіки*/
                    String getText;
                    /** Текст повідомлення*/
                    String alertMessage = "Немає даних !";

                    try {
                        WebElement text = driver.findElement(By.cssSelector("#chart-forecast > svg > g:nth-child(2) > text"));
                        getText  = text.getText();
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо отримати текст повідомлення з графіків");
                    }

                    if(!alertMessage.equalsIgnoreCase(getText)){
                        throw new RuntimeException("Відсутнє повідомлення про відсутність даних");
                    }

                    break;
                }
                case 2: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                            "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

                    getFunc.waitForElementByXpath("//div[1]/div/div/div[2]/div[1]/input", 10, driver);

                    getFunc.getOffDefaultGraphics(driver);
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[1]/div[5]",
                            "Не можливо натиснути на чекбокс графік цін");

                    getFunc.checkBoxDisabledAlertByXpath(driver, "//*[@id=\"Price\"]");

                    /** Текст елемена, який виводить повідомлення на робочий стіл графіки*/
                    String getText;
                    /** Текст повідомлення*/
                    String alertMessage = "Невибрано графік з даними !";

                    try {
                        WebElement text = driver.findElement(By.cssSelector("#chart-forecast > svg > g:nth-child(2) > text"));
                        getText  = text.getText();
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо отримати текст повідомлення з графіків");
                    }

                    if(!alertMessage.equalsIgnoreCase(getText)){
                        throw new RuntimeException("Відсутнє повідомлення про відсутність даних");
                    }
                    break;
                }
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "графік продажів", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void ZG_salesGraphicGraphicsDesktopTest_32() throws Exception {
        startBrowser();

        getFunc.catchStatusLoadPage();
        driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
        getFunc.getSearchArticle("1158", driver);

        getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 10, driver);
        getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

        getFunc.waitForElementByXpath("//div[1]/div/div/div[2]/div[1]/input", 10, driver);

        getFunc.checkBoxDisabledAlertByXpath(driver, "//*[@id=\"AllPSales\"]");

        System.gc();
//        stopBrowser();
    }


    @Test(testName = "збереження в історії чекбоксів робочого столу Графіків", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void ZH_checkBoxHistoryGraphicsDesktopTest_33() throws Exception {
        startBrowser();
        String inputValues = "609642";

        getFunc.catchStatusLoadPage();
        driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
        getFunc.getSearchArticle(inputValues, driver);

        getFunc.waitForElementByXpath("//section[1]/div/div[5]/div", 20, driver);
        getFunc.clickSelectorByXpath(driver, "//section[1]/div/div[5]/div",
                "не можливо натиснути кнопку інфоблок робочий стіл Графіки");

        getFunc.waitForElementByXpath("//div[1]/div/div/div[2]/div[1]/input", 20, driver);
        getFunc.waitForElementByXpath("//div[2]/label", 20, driver);
        getFunc.clickSelectorByXpath(driver,"//div[2]/label",
                "Не можливо натиснути на чекбокс потенційні продажі");

        getFunc.clickSelectorByXpath(driver,"//div[1]/div[4]/label",
                "Не можливо натиснути на чекбокс Прогноз");

        /** Текст елемена, який виводить повідомлення на робочий стіл графіки*/
        String getText;
        /** Текст повідомлення*/
        String alertMessage = "Невибрано графік з даними !";

        try {
            WebElement text = driver.findElement(By.cssSelector("#chart-forecast > svg > g:nth-child(2) > text"));
            getText  = text.getText();
        } catch (Exception e){
            throw new RuntimeException("Не можливо отримати текст повідомлення з графіків");
        }

        if(!alertMessage.equalsIgnoreCase(getText)){
            throw new RuntimeException("Відсутнє повідомлення про відсутність даних");
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "елемент \"звіт\"", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void ZI_reportDesktopTest_34() throws Exception {
        startBrowser();
        String[] inputValues = {"1158","1158","1158","1158","1158"};
        int arrayLength = inputValues.length;
        for (int i = 0; i < arrayLength; i++) {
            switch (i) {
                case 0: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.openReportDeskTop(driver);
                    break;
                }
                case 1:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.openReportDeskTop(driver);

                    getFunc.waitForElementByXpath("//span/i", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//span/i",
                            "Не можливо натиснути на кнопку - Х закриття робочого столу");
                    getFunc.checkForDeskTopClose(driver,"//section[2]/div/div");
                    break;
                }
                case 2:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.openReportDeskTop(driver);

                    getFunc.waitForElementByXpath("//section[1]/div/div[1]/div", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");

                    getFunc.checkForDeskTopOpen(driver,"//section[2]/div/div");
                    getFunc.checkForDeskTopOpen(driver,"//section[2]/div/div[2]");
                    Thread.sleep(1500);
                    break;
                }
                case 3:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.openReportDeskTop(driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");

                    getFunc.checkForDeskTopOpen(driver,"//section[2]/div/div");
                    getFunc.checkForDeskTopOpen(driver,"//section[2]/div/div[2]");
                    Thread.sleep(1500);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");

                    getFunc.checkForDeskTopClose(driver, "//section[2]/div/div[2]");
                    break;
                }
                case 4:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.openReportDeskTop(driver);

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[1]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (ПРОГНОЗ)");

                    getFunc.checkForDeskTopOpen(driver,"//section[2]/div/div");
                    getFunc.checkForDeskTopOpen(driver,"//section[2]/div/div[2]");

                    getFunc.clickSelectorByXpath(driver,"//section[1]/div/div[2]/div",
                            "Не можливо здійснити клік на елемент інфоблоку (Акції)");

                    getFunc.checkForDeskTopClose(driver,"//*[@id=\"single-button\"]");
                    break;
                }
            }
        }
        System.gc();
//        stopBrowser();
    }

    @Test(testName = "відображення інформації у робочому столі \"Звіти\"", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void ZJ_showInfoReportDesktopTest_35() throws Exception {
        startBrowser();
        DateFormat dateFormat = new SimpleDateFormat("MM");
        Date date = new Date();
        int monthCheckVal = Integer.parseInt(dateFormat.format(date)) - 1;

        String[] monthXpath = {
                "//tr[1]/td[1]/button",
                "//tr[1]/td[2]/button",
                "//tr[1]/td[3]/button",
                "//tr[2]/td[1]/button",
                "//tr[2]/td[2]/button",
                "//tr[2]/td[3]/button",
                "//tr[3]/td[1]/button",
                "//tr[3]/td[2]/button",
                "//tr[3]/td[3]/button",
                "//tr[4]/td[1]/button",
                "//tr[4]/td[2]/button",
                "//tr[4]/td[3]/button",
                "//th[2]/button"};

        String[] inputValues = {"1158", "1158", "1159", "1158", "125036, 125037, 125038, 125039, 125040, 125041"};
        int arrayLength = inputValues.length;
        for (int i = 0; i < arrayLength; i++) {
            switch (i) {
                case 0: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.openReportDeskTop(driver);
                    break;
                }
                case 1: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//div[2]/div[2]/span/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[2]/span/button",
                            "Не можливо здійснити клік на кнопку (Дата закінчення періоду)");

                    Thread.sleep(1500);
                    getFunc.clickSelectorByXpath(driver,"//th[3]/button",
                            "Не можливо натиснути кнопку переключення до наступного місяця > (дата початку періоду)");

                    Thread.sleep(1500);
                    getFunc.clickSelectorByXpath(driver, monthXpath[monthCheckVal],
                            "Не можливо вибрати останній місяць періоду закінчення");

                    Thread.sleep(1500);
                    getFunc.clickSelectorByXpath(driver, "//month-picker/div/div[4]/button",
                            "неможливо натиснути кнопку панелі фільтрів: (Застосувати дати)");

                    getFunc.openReportDeskTop(driver);
                    break;
                }
                case 2:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.openReportDeskTop(driver);
                    break;
                }
                case 3:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//div[2]/div[2]/span/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[2]/div[2]/span/button",
                            "Не можливо здійснити клік на кнопку (Дата закінчення періоду)");

                    getFunc.clickSelectorByXpath(driver,"//th[3]/button",
                            "Не можливо натиснути кнопку переключення до наступного місяця > (дата початку періоду)");

                    getFunc.clickSelectorByXpath(driver, monthXpath[monthCheckVal],
                            "Не можливо вибрати останній місяць періоду закінчення");

                    getFunc.clickSelectorByXpath(driver, "//month-picker/div/div[4]/button",
                            "неможливо натиснути кнопку панелі фільтрів: (Застосувати дати)");

                    getFunc.openReportDeskTop(driver);

                    getFunc.switchToAnalyticReport(driver);
                    break;
                }
                case 4:{
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);

                    getFunc.inputMultiArticles(driver, inputValues[i]);

                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");

                    getFunc.waitForElementByXpath("html/body/div[3]/div/form/div[4]/div/ul/li/span[2]", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");

                    getFunc.openReportDeskTop(driver);


                    getFunc.waitForElementByXpath("//div[4]/button/i", 20, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[4]/button/i",
                            "не можливо натиснути кнопку перехід до наступного артикула (>)");

                    getFunc.waitForElementByXpath("//div/div[5]/button/i", 10, driver);

                    getFunc.clickSelectorByXpath(driver,"//div/div[5]/button/i",
                            "не можливо натиснути кнопку перехід до наступного артикула (>>)");

                    getFunc.waitForElementByXpath("//div/div[5]/button/i", 20, driver);
                    getFunc.waitForElementByXpath("//div/section[2]/div/div", 20, driver);

                    /** Вебелемент колонка - ім'я*/
                    WebElement columnName = driver.
                            findElement(By.xpath("//div/section[2]/div/div"));
                    if(!columnName.getText().contains("125041")){
                        throw new RuntimeException("Не відображається шукана к-сть артикулів в аналітичному звіті");
                    }
                    break;
                }
            }
        }
        System.gc();
//        stopBrowser();
    }


    @Test(testName = "кнопка \"завантажити звіт\"", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void ZK_downloadReportButtonDesktopTest_36() throws Exception {
        startBrowser();
        String inputValue = "1158";

        getFunc.catchStatusLoadPage();
        driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
        getFunc.getSearchArticle(inputValue, driver);

        getFunc.openReportDeskTop(driver);

        getFunc.clickSelectorByXpath(driver, "//*[@id=\"single-button\"]",
                "Не можливо натиснути кнопку завантажити звіт");

        /** Змінна на перевірку існуючого ім'я класу */
        Boolean classExist;
        try {
            WebElement el = driver.findElement(By.xpath("//section[2]/div/div/div[3]/div"));
            classExist = getFunc.hasClass(el, "open");
        } catch (Exception e){
            throw new RuntimeException("Не можливо отримати стан кнопки Завнтажити звіт");
        }

        if(!classExist){
            throw new RuntimeException("Не доступні варіанти завантаження звіту");
        }

        getFunc.clickSelectorByXpath(driver, "//div[3]/div/ul/li[2]/a",
                "Не можливо вибрати завантаження місяців як стовпців");

        getFunc.clickSelectorByXpath(driver, "//*[@id=\"single-button\"]",
                "Не можливо натиснути кнопку завантажити звіт");

        getFunc.clickSelectorByXpath(driver, "//div[3]/div/ul/li[1]/a",
                "Не можливо вибрати завантаження місяців як рядків");

        Thread.sleep(3500);
        /** Перевірка на існування згенерованих файлів звіту*/
        getFunc.isFileExist("reports.xls", downloadFilepath);
        getFunc.isFileExist("reports (1).xls", downloadFilepath);

        FileUtils.cleanDirectory(downloadFilepath.toFile());

        downloadFilepath.toFile().delete();

        System.gc();
//        stopBrowser();
    }


    @Test(testName = "генерація \"Аналітичного звіту\"", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void ZL_analyticGenerationReportDesktopTest_37() throws Exception {
        startBrowser();

        String[] inputValues = {"308580", "Хліб", "рошен"};
        int arrayLength = inputValues.length;
        for (int i = 0; i < arrayLength; i++) {
            switch (i) {
                case 0: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.getSearchArticle(inputValues[i], driver);

                    getFunc.openReportDeskTop(driver);
                    getFunc.switchToAnalyticReport(driver);

                    /** Вебелемент (колонка) група - аналітичний звіт*/
                    WebElement columnName = driver.
                            findElement(By.xpath("//section[2]/div/div"));
                    if(!columnName.getText().contains("Група XYZ")){
                        throw new RuntimeException("Не можливо знайти таблицю аналітичного звіту");
                    }
                    break;
                }
                case 1: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
                    getFunc.searchProductList(inputValues[i], driver);
                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");

                    getFunc.openReportDeskTop(driver);
                    getFunc.switchToAnalyticReport(driver);

                    Thread.sleep(1500);
                    /** Вебелемент (колонка) тип моделі - аналітичний звіт*/
                    getFunc.waitForElementByXpath("//section[2]/div/div", 20, driver);
                    WebElement desktopElement = driver.
                            findElement(By.xpath("//section[2]/div/div"));
                    if(!desktopElement.getText().contains("Кростон") && !desktopElement.getText().contains("АримаПустая")
                            && !desktopElement.getText().contains("holt")){
                        throw new RuntimeException("Не можливо знайти дані в аналітичному звіті");
                    }
                    break;
                }
                case 2: {
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);
                    getFunc.loginUser(userLogin, userPassword, currentDomain, driver);

                    getFunc.openPopUpWindow(driver, baseUrl, userLogin, userPassword, currentDomain);
                    getFunc.searchProvider(inputValues[i], driver);

                    getFunc.waitForElementByXpath("//div[3]/button[2]", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[3]/button[2]",
                            "Не можливо натиснути кнопку Відібрати POP UP Меню");

                    getFunc.waitForElementByXpath("//ul/li[1]/span[1]", 10, driver);
                    getFunc.clickSelectorByXpath(driver,"//div[6]/button[2]",
                            "Не можливо натиснути кнопку ОК POP UP Menu");

                    getFunc.openReportDeskTop(driver);
                    getFunc.switchToAnalyticReport(driver);
                    Thread.sleep(3500);
                    /** Вебелемент (колонка) тип моделі - аналітичний звіт*/
                    WebElement desktopElement = driver.
                            findElement(By.xpath("//section[2]/div/div"));
                    if(!desktopElement.getText().contains("Кростон")){
                        throw new RuntimeException("Не можливо знайти дані в аналітичному звіті");
                    }
                    break;
                }
            }
        }
    }


    @Test(testName = " збереження \"Аналітичного звіту\"", groups = {"workDesktop"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void ZM_savingAnalyticReportDesktopTest_38() throws Exception {
        startBrowser();


        String inputValues = "609642";
        getFunc.catchStatusLoadPage();
        driver.get(baseUrl);
        getFunc.loginUser(userLogin, userPassword, currentDomain, driver);
        getFunc.getSearchArticle(inputValues, driver);

        getFunc.openReportDeskTop(driver);
        getFunc.switchToAnalyticReport(driver);

        /** Вебелемент (колонка) група - аналітичний звіт*/
        WebElement columnName = driver.
                findElement(By.xpath("//section[2]/div/div"));
        if (!columnName.getText().contains("Група XYZ")) {
            throw new RuntimeException("Не можливо знайти таблицю аналітичного звіту");
        }

        getFunc.clickSelectorByXpath(driver, "//div[3]/span",
                "Не можливо натиснути кнопку завантажити звіт");

        Thread.sleep(3500);
        getFunc.clickSelectorByXpath(driver, "//div[3]/span",
                "Не можливо натиснути кнопку завантажити звіт");

        Thread.sleep(3500);
        /** Перевірка на існування згенерованих файлів звіту*/
        getFunc.isFileExist("reports.xls", downloadFilepath);
        getFunc.isFileExist("reports (1).xls", downloadFilepath);

        FileUtils.cleanDirectory(downloadFilepath.toFile());

        downloadFilepath.toFile().delete();

        System.gc();
//        stopBrowser();
    }

    @Test(testName = "Авторизація, тестування доменів", groups = {"loginPage"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void ZN_domainAuthorizationTest_39() throws Exception {

        /**Змінні для заповнення полів логіна, пароля та вибору домена*/
        String [] authorizationLogins = {"test_ok", "test_ou", "test_bk", "test_bu"};
        String [] authorizationPasswords = {"OfKv1Te@st", "OfUk2Te@st", "BsKv3Te@st", "BsUk4Te@st"};
        String [] authorizationDomains = {"OFFICEKIEV", "OFFICEUKRAINE", "BUSINESSKIEV", "BUSINESSUKRAINE"};

        /**Лічильник кількості змінних для визначення к-сті кроків тесту*/
        int testCount = authorizationLogins.length;
        for(int i = 0; i < testCount; i++){
            startBrowser();
            getFunc.catchStatusLoadPage();
            driver.get(baseUrl);

            getFunc.loginUser(authorizationLogins[i], authorizationPasswords[i], authorizationDomains[i], driver);

            stopBrowser();

        }

        /**Змінна для корегування вибору домена з селекту*/
        int domainArrayIndex;

        startBrowser();
        for(int i = 0; i < testCount; i++){
            getFunc.catchStatusLoadPage();
            driver.get(baseUrl);

            if(i < testCount - 1){
                domainArrayIndex = i + 1;
            } else
                domainArrayIndex = 0;

            getFunc.testLoginUser(authorizationLogins[i], authorizationPasswords[i], authorizationDomains[domainArrayIndex], driver);
            getFunc.waitForElementByXpath("//ui-view/div/div/form/div",10, driver);

            WebElement errorMessage;
            try {
                errorMessage = driver.findElement(By.xpath("//ui-view/div/div/form/div"));
            } catch (Error e){
                throw new RuntimeException("Не можливо звернутись до вебелемента помилки");
            }

            if(!errorMessage.getText().equalsIgnoreCase("Неверное имя пользователя или пароль")){
                throw new RuntimeException("Відсутнє повідомлення про невірне введення пароля або логіна");
            }

        }
    }

    @Test(testName = "Авторизація, тестування поля: логін ", groups = {"loginPage"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void ZO_loginAuthorizationTest_40() throws Exception {

        String[] authorizationLogins = {"test_ok", "test", "", "test_bu", "^\\d+$"};
        String[] authorizationPasswords = {"OfKv1Te@st", "OfUk2Te@st", "BsKv3Te@st", "BsUk4Te@st", "testpass"};
        String[] authorizationDomains = {"OFFICEKIEV", "OFFICEUKRAINE", "BUSINESSKIEV", "BUSINESSUKRAINE", "OFFICEUKRAINE"};
        String[] inputValues = {"___", "***", "*?*", "/*-+Ж/\\^[a-z0-9_-]{3,1'5}$\"", "0000"}; //" ",


        int testCount = authorizationLogins.length;
        for(int i = 0; i < testCount; i++){
            switch (i){
                case 0:{
                    startBrowser();
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);

                    getFunc.loginUser(authorizationLogins[i], authorizationPasswords[i], authorizationDomains[i], driver);
                    stopBrowser();

                    break;
                }

                case 1:{
                    startBrowser();
                    driver.get(baseUrl);

                    getFunc.testLoginUser(authorizationLogins[i], authorizationPasswords[i], authorizationDomains[i], driver);

                    getFunc.waitForElementByXpath("//ui-view/div/div/form/div", 10, driver);
                    WebElement errorMessage;
                    try {
                        errorMessage = driver.findElement(By.xpath("//ui-view/div/div/form/div"));
                    } catch (Error e){
                        throw new RuntimeException("Не можливо звернутись до вебелемента помилки");
                    }

                    if(!errorMessage.getText().equalsIgnoreCase("Неверное имя пользователя или пароль")){
                        throw new RuntimeException("Відсутнє повідомлення про невірне введення пароля або логіна");
                    }
                    stopBrowser();
                    break;
                }

                case 2:{
                    startBrowser();
                    driver.get(baseUrl);

                    getFunc.testLoginUser(authorizationLogins[i], authorizationPasswords[i], authorizationDomains[i], driver);
                    getFunc.checkRequiredField(driver, "/html/body/ui-view/div/div/form/input[1]");
                    stopBrowser();
                    break;

                }

                case 3:{
                    startBrowser();
                    driver.get(baseUrl);

                    getFunc.loginUser(authorizationLogins[i], authorizationPasswords[i], authorizationDomains[i], driver);

                    driver.navigate().back();
                    getFunc.waitForElementByXpath("/html/body/ui-view/div/div/form/input[1]", 10, driver);

                    driver.navigate().forward();

                    getFunc.waitForElementByXpath("//month-picker/div/div[4]/button", 10, driver);
                    getFunc.isElementDisabledAlertByXpath("//month-picker/div/div[4]/button", driver);

                    stopBrowser();
                    break;
                }

                case 4:{
                    startBrowser();
                    driver.get(baseUrl);

                    for(int j = 0; j < inputValues.length; j++){
                        if(j > 0){
                            startBrowser();
                            driver.get(baseUrl);
                        }

                        getFunc.inputValue(driver, "//form/input[1]", inputValues[j]);
                        getFunc.inputValue(driver, "//form/input[2]", authorizationPasswords[i]);

                        getFunc.waitForElementByXpath("//form/button", 10, driver);
                        getFunc.clickSelectorByXpath(driver, "//form/button", "Не можливо здійснити клік на кнопку Вход при логінізації");

                        WebElement errorMessage;
                        getFunc.waitForElementByXpath("//ui-view/div/div/form/div", 10, driver);
                        try {
                            errorMessage = driver.findElement(By.xpath("//ui-view/div/div/form/div"));
                        } catch (Error e){
                            throw new RuntimeException("Не можливо звернутись до вебелемента помилки");
                        }

                        if(!errorMessage.getText().equalsIgnoreCase("Неверное имя пользователя или пароль")){
                            throw new RuntimeException("Відсутнє повідомлення про невірне введення пароля або логіна");
                        }

                        stopBrowser();
                    }
                    break;
                }
            }
        }

    }


    @Test(testName = "Авторизація, тестування поля: пароль ", groups = {"loginPage"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void ZP_passwordAuthorizationTest_41() throws Exception {

        String[] authorizationLogins = {"test_ok", "test_ou", "test_bk", "test_bu", "test_regulars", "test_encript", ""};
        String[] authorizationPasswords = {"OfKv1Te@st", "test", "", "BsUk4Te@st", "testpass", "test_encript", "test_copy_password"};
        String[] authorizationDomains = {"OFFICEKIEV", "OFFICEUKRAINE", "BUSINESSKIEV", "BUSINESSUKRAINE", "OFFICEUKRAINE", "OFFICEUKRAINE"};
        String[] inputValues = {"___", "***", "*?*", "/*-+Ж/\\^[a-z0-9_-]{3,1'5}$\"", "0000"}; //" ",

        int testCount = authorizationLogins.length;
        for(int i = 0; i < testCount; i++){
            switch (i){
                case 0:{
                    startBrowser();
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);

                    getFunc.loginUser(authorizationLogins[i], authorizationPasswords[i], authorizationDomains[i], driver);
                    stopBrowser();
                    break;
                }

                case 1:{
                    startBrowser();
                    driver.get(baseUrl);

                    getFunc.testLoginUser(authorizationLogins[i], authorizationPasswords[i], authorizationDomains[i], driver);

                    getFunc.waitForElementByXpath("//ui-view/div/div/form/div", 10, driver);
                    WebElement errorMessage;
                    try {
                        errorMessage = driver.findElement(By.xpath("//ui-view/div/div/form/div"));
                    } catch (Error e){
                        throw new RuntimeException("Не можливо звернутись до вебелемента помилки");
                    }

                    if(!errorMessage.getText().equalsIgnoreCase("Неверное имя пользователя или пароль")){
                        throw new RuntimeException("Відсутнє повідомлення про невірне введення пароля або логіна");
                    }
                    stopBrowser();
                    break;
                }

                case 2:{
                    startBrowser();
                    driver.get(baseUrl);

                    getFunc.testLoginUser(authorizationLogins[i], authorizationPasswords[i], authorizationDomains[i], driver);
                    getFunc.checkRequiredField(driver, "/html/body/ui-view/div/div/form/input[2]");
                    stopBrowser();

                    break;

                }

                case 3:{
                    startBrowser();
                    driver.get(baseUrl);

                    getFunc.loginUser(authorizationLogins[i], authorizationPasswords[i], authorizationDomains[i], driver);

                    /** Натискання кнопки браузера - повернення до попередньої сторінки*/
                    driver.navigate().back();
                    getFunc.waitForElementByXpath("/html/body/ui-view/div/div/form/input[1]", 10, driver);

                    /** Натискання кнопки браузера - перехід до наступної сторінки*/
                    driver.navigate().forward();

                    getFunc.waitForElementByXpath("//month-picker/div/div[4]/button", 10, driver);
                    getFunc.isElementDisabledAlertByXpath("//month-picker/div/div[4]/button", driver);

                    stopBrowser();
                    break;
                }

                case 4:{
                    startBrowser();
                    driver.get(baseUrl);

                    for(int j = 0; j < inputValues.length; j++){
                        if(j > 0){
                            startBrowser();
                            driver.get(baseUrl);
                        }

                        getFunc.inputValue(driver, "//form/input[1]", authorizationLogins[i]);
                        getFunc.inputValue(driver, "//form/input[2]", inputValues[j]);

                        getFunc.waitForElementByXpath("//form/button", 10, driver);
                        getFunc.clickSelectorByXpath(driver, "//form/button", "Не можливо здійснити клік на кнопку Вход при логінізації");

                        WebElement errorMessage;
                        getFunc.waitForElementByXpath("//ui-view/div/div/form/div", 10, driver);
                        try {
                            errorMessage = driver.findElement(By.xpath("//ui-view/div/div/form/div"));
                        } catch (Error e){
                            throw new RuntimeException("Не можливо звернутись до вебелемента помилки");
                        }

                        if(!errorMessage.getText().equalsIgnoreCase("Неверное имя пользователя или пароль")){
                            throw new RuntimeException("Відсутнє повідомлення про невірне введення пароля або логіна");
                        }
                        stopBrowser();
                    }
                    break;
                }

                case 5:{
                    startBrowser();
                    driver.get(baseUrl);

                    getFunc.inputValue(driver, "//form/input[1]", authorizationLogins[i]);
                    getFunc.inputValue(driver, "//form/input[2]", authorizationPasswords[i]);

                    WebElement inputPassword;
                    boolean isEncrypted;
                    try {
                        inputPassword = driver.findElement(By.xpath("//form/input[2]"));
                        isEncrypted = inputPassword.getAttribute("type").equals("password");
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо перевірити тип поля пароль");
                    }

                    if(!isEncrypted){
                        throw new RuntimeException("Невірний тип поля пароль");
                    }
                }

                case 6:{
                    startBrowser();
                    driver.get(baseUrl);

                    getFunc.waitForElementByXpath("//form/input[2]", 10, driver);
                    getFunc.inputValue(driver, "//form/input[2]", authorizationPasswords[i]);

                    WebElement inputPassword;
                    try {
                        inputPassword = driver.findElement(By.xpath("//form/input[2]"));
                    } catch (Exception e){
                        throw new RuntimeException("Не можливо отримати веб елемент поля пароль");
                    }

                    /** Виділення тексту в полі об'єкта inputPassword */
                    inputPassword.sendKeys(Keys.chord(Keys.CONTROL, "a"));

                    /** Виділення тексту в полі об'єкта inputPassword */
                    inputPassword.sendKeys(Keys.chord(Keys.CONTROL, "c"));

                    String passwordField = (String) Toolkit.getDefaultToolkit()
                            .getSystemClipboard().getData(DataFlavor.stringFlavor);
                    if(passwordField.equalsIgnoreCase("test_copy_password"))
                        throw new RuntimeException("Можливо скопіювати пароль в буфер обміну");

                    break;
                }
            }
        }

    }

    @Test(testName = "Авторизація, кнопка Вхід", groups = {"loginPage"},
            retryAnalyzer = FozzySalesForecastTest.class)
    public void ZQ_loginButtonAuthorizationTest_42() throws Exception {
        String authorizationLogin = "test_ok";
        String authorizationPassword = "OfKv1Te@st";
        String authorizationDomain = "OFFICEKIEV";

        for(int i = 0; i < 2; i++) {
            switch (i) {
                case 0:{
                    startBrowser();
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);

                    getFunc.waitForElementByXpath("//form/button", 10, driver);
                    getFunc.clickSelectorByXpath(driver, "//form/button", "Не можливо натиснути кнопку \"Войти\"");
                    startBrowser();
                    break;
                }
                case 1:{
                    startBrowser();
                    getFunc.catchStatusLoadPage();
                    driver.get(baseUrl);

                    getFunc.loginUser(authorizationLogin, authorizationPassword, authorizationDomain, driver);
                    stopBrowser();
                    break;
                }
            }


        }

    }


}