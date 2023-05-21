package uk.gibby.dsl.serialization
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.encoding.*
import uk.gibby.dsl.model.Linked


class LinkedSerializer<T : Any>(
    private val tSerializer: KSerializer<T>
) : KSerializer<Linked<T>> {

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(tSerializer.descriptor.serialName + "Link") {
        element("id", String.serializer().descriptor)
        tSerializer.descriptor.elementNames.forEachIndexed { index, name ->
            val descriptor = tSerializer.descriptor.getElementDescriptor(index)
            element(name, descriptor)
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
        when(value) {
            is Linked.Reference -> encoder.encodeString(value.id)
            is Linked.Actual -> {
                encoder.encodeSerializableValue(tSerializer, value.result)
            }
        }
    }
}



