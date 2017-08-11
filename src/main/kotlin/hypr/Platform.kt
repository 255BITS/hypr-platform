package hypr

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.ModelAndView
import spark.Spark.*
import com.squareup.moshi.*
import spark.TemplateEngine
import spark.template.velocity.VelocityTemplateEngine

val logger: Logger = LoggerFactory.getLogger("main")

data class Generator(val name: String) 
fun main(args: Array<String>) {

    val application = Platform(8080)
    application.start()
}


class Platform(val port: Int = 8080) {

    val renderEngine: TemplateEngine
        get() = VelocityTemplateEngine()

    fun start() {
      
        port(8080)

        get("/v1/models.json") { req, res ->
          res.type("application/json")
          val results = ArrayList<Generator>()
          results.add(Generator("TEST"))
          Gson().toJson(results)
        }
    }

    fun render(templateName: String,
               model: Any = emptyMap<String, String>()) = renderEngine.render(ModelAndView(model, "templates/$templateName"))

}

