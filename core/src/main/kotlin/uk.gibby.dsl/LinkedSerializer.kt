package uk.gibby.dsl
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.elementDescriptors
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure

@Serializable(with = ServiceResultSerializer::class)
sealed class ServiceResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : ServiceResult<T>()
    data class Error(val exceptionMessage: String?) : ServiceResult<Nothing>()
}

class ServiceResultSerializer<T : Any>(
    tSerializer: KSerializer<T>
) : KSerializer<ServiceResult<T>> {
    @Serializable
    @SerialName("ServiceResult")
    data class ServiceResultSurrogate<T : Any> @OptIn(ExperimentalSerializationApi::class) constructor(
        val type: Type,
        // The annotation is not necessary, but it avoids serializing "data = null"
        // for "Error" results.
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        val data: T? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        val exceptionMessage: String? = null
    ) {
        enum class Type { SUCCESS, ERROR }
    }

    private val surrogateSerializer = ServiceResultSurrogate.serializer(tSerializer)

    override val descriptor: SerialDescriptor = surrogateSerializer.descriptor

    override fun deserialize(decoder: Decoder): ServiceResult<T> {
        val surrogate = surrogateSerializer.deserialize(decoder)
        return when (surrogate.type) {
            ServiceResultSurrogate.Type.SUCCESS ->
                if (surrogate.data != null)
                    ServiceResult.Success(surrogate.data)
                else
                    throw SerializationException("Missing data for successful result")
            ServiceResultSurrogate.Type.ERROR ->
                ServiceResult.Error(surrogate.exceptionMessage)
        }
    }

    override fun serialize(encoder: Encoder, value: ServiceResult<T>) {
        val surrogate = when (value) {
            is ServiceResult.Error -> ServiceResultSurrogate(
                ServiceResultSurrogate.Type.ERROR,
                exceptionMessage = value.exceptionMessage
            )
            is ServiceResult.Success -> ServiceResultSurrogate(
                ServiceResultSurrogate.Type.SUCCESS,
                data = value.data
            )
        }
        surrogateSerializer.serialize(encoder, surrogate)
    }
}


class LinkedSerializer<T : Any>(
    private val tSerializer: KSerializer<T>
) : KSerializer<Linked<T>> {

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(tSerializer.descriptor.serialName + "Link"){
        tSerializer.descriptor.elementDescriptors.forEach {
            element("id", String.serializer().descriptor)
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
