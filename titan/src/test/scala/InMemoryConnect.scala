import com.thinkaurelius.titan.core.TitanGraph

trait InMemoryConnect {
  def connect(): TitanGraph = {
    import org.apache.commons.configuration.BaseConfiguration
    val conf = new BaseConfiguration()
    conf.setProperty("storage.backend","inmemory")
    import com.thinkaurelius.titan.core.TitanFactory
    TitanFactory.open(conf)
  }
}
