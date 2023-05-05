package uk.gibby.dsl.serialization
import kotlinx.serialization.*
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.elementDescriptors
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import uk.gibby.dsl.driver.surrealJson
import uk.gibby.dsl.model.Linked
import uk.gibby.dsl.model.rpc.RpcResponse



class LinkedSerializer<T : Any>(
    private val tSerializer: KSerializer<T>
) : KSerializer<Linked<T>> {

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(tSerializer.descriptor.serialName + "Link") {
        element("id", String.serializer().descriptor)
        tSerializer.descriptor.elementDescriptors.forEach {
            println("Type: ${this::class} SerialName: ${it.serialName}")
            element(it.serialName, it)
        }
    }

    override fun deserialize(decoder: Decoder): Linked<T> {
        return try {
            val result = decoder.decodeSerializableValue(tSerializer)
            var id: String? = null
            decoder.decodeStructure(descriptor) {
                id = decodeStringElement(descriptor, 0)
            }
            Linked.Actual(id!!, result)
        } catch (e: Exception) {
            Linked.Reference(decoder.decodeString())
        }
    }

    override fun serialize(encoder: Encoder, value: Linked<T>) {
        TODO()
    }
}



class RpcResponseSerializer() : KSerializer<RpcResponse> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("rpc") {
        element("id", String.serializer().descriptor)
        element("result", JsonElement.serializer().descriptor)
        element("error", JsonElement.serializer().descriptor)
    }
    override fun deserialize(decoder: Decoder): RpcResponse {
        var id: String? = null
        var result: JsonElement? = null
        var error: JsonElement? = null
        decoder.decodeStructure(descriptor){
            while (true) {
                when(val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeStringElement(descriptor, 0)
                    1 -> result = decodeSerializableElement(descriptor, 0, JsonElement.serializer())
                    2 -> error = decodeSerializableElement(descriptor, 0, JsonElement.serializer())
                    CompositeDecoder.DECODE_DONE -> break
                }
            }
        }
        return if(error != null) {
            RpcResponse.Error(id!!, surrealJson.encodeToJsonElement(error))
        } else RpcResponse.Success(id!!, result ?: surrealJson.encodeToJsonElement(String.serializer().nullable, null))
    }

    override fun serialize(encoder: Encoder, value: RpcResponse) {
        TODO("Not yet implemented")
    }

}
