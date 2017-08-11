package hypr

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.ModelAndView
import spark.Spark.*
import com.squareup.moshi.*
import spark.TemplateEngine
import spark.template.velocity.VelocityTemplateEngine

import org.ektorp.*
import org.ektorp.http.*
import org.ektorp.impl.*
import org.ektorp.support.*
import com.fasterxml.jackson.annotation.*

import java.util.UUID

val logger: Logger = LoggerFactory.getLogger("main")

@JsonIgnoreProperties("id", "revision")
data class GeneratorEvent(
  @JsonProperty("name") var name: String?, 
  @JsonProperty("type" ) var type: String?,
  @JsonProperty("_id") var id:String? = UUID.randomUUID().toString(),
  @JsonProperty("_rev") var revision: String? = null,
  @JsonProperty("viewer_operation" ) var viewer_operation: String? = "FaceViewer",
  @JsonProperty("input_width" ) var input_width: Int? = 128,
  @JsonProperty("input_height" ) var input_height: Int? = 128,
  @JsonProperty("input_channels" ) var input_channels: Int? = 3,
  @JsonProperty("input_type" ) var input_type: String? = "image",
  @JsonProperty("output_type" ) var output_type: String? = "image",
  @JsonProperty("output_width" ) var output_width: Int? = 128,
  @JsonProperty("output_height" ) var output_height: Int? = 128,
  @JsonProperty("output_channels" ) var output_channels: Int? = 3
) 


fun main(args: Array<String>) {
    val application = Platform(8080)
    application.start()
}


class GeneratorRepository(db: CouchDbConnector) : CouchDbRepositorySupport<GeneratorEvent>(GeneratorEvent::class.java, db) {
}

class Platform(val port: Int = 8080) {

    val renderEngine: TemplateEngine
        get() = VelocityTemplateEngine()

    fun start() {
        val httpClient:HttpClient = StdHttpClient.Builder().url("http://localhost:5984").build()

        val dbInstance:CouchDbInstance  = StdCouchDbInstance(httpClient)
        val db:CouchDbConnector = StdCouchDbConnector("generators", dbInstance)

        var repo :GeneratorRepository = GeneratorRepository(db)

        db.createDatabaseIfNotExists()

        port(8080)

        get("/v1/generators.json") { req, res ->
          res.type("application/json")
          val results = repo.getAll()
          Gson().toJson(results)
        }
        post("/v1/generators.json") { req, res ->
          res.type("application/json")
          // user uploads model to platform
          repo.add(GeneratorEvent("Test", "new"))
          // user downloads model
          repo.add(GeneratorEvent("Test", "download"))
          // user purchases model
          repo.add(GeneratorEvent("Test", "purchase"))
          // user rates model
          repo.add(GeneratorEvent("Test", "rating"))
        }
    }

    fun render(templateName: String,
               model: Any = emptyMap<String, String>()) = renderEngine.render(ModelAndView(model, "templates/$templateName"))

}

