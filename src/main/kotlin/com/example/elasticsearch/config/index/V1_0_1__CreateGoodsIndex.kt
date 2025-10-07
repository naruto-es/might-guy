import com.example.elasticsearch.config.property.ElasticSearchProperty
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties
import org.springframework.web.client.RestTemplate

class V1_0_1__CreateGoodsIndex(
    private val elasticSearchProperty: ElasticSearchProperty,
    private val restTemplate: RestTemplate,
) : BaseJavaMigration() {
    override fun migrate(context: Context) {
        val body = """
            {
              "settings": {
                "analysis": {
                  "analyzer": {
                    "korean_analyzer": {
                      "type": "custom",
                      "tokenizer": "nori_tokenizer"
                    }
                  }
                }
              },
              "mappings": {
                "properties": {
                  "id": { "type": "long" },
                  "name": { "type": "keyword" },
                  "price": { "type": "double" },
                  "description": {
                    "type": "text",
                    "analyzer": "korean_analyzer",
                    "search_analyzer": "korean_analyzer"
                  }
                }
              }
            }
        """.trimIndent()

        restTemplate.put("${elasticSearchProperty.uri}/goods", body)
    }
}