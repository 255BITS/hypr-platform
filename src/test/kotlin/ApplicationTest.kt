
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import io.kotlintest.TestCaseContext
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class ApplicationTest: StringSpec() {

    val port = 8080

    init {
        "server should be started" {
            val webClient = WebClient()
            val page: HtmlPage = webClient.getPage("http://localhost:${port}")

            page.body.asText() shouldBe "You're ready to go!"
        }
    }

    override fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
        val application = Application(port)
        application.ignite()

        test()

        application.extinguish()
    }
}