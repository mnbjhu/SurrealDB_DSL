package uk.gibby.dsl.serialization.row

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import uk.gibby.dsl.model.rows.Row2


class Row2Serializer<T : Any, U: Any>(
    private val tSerializer: KSerializer<T>,
    private val uSerializer: KSerializer<U>
) : KSerializer<Row2<T, U>> {

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Row2<${tSerializer.descriptor.serialName}, ${uSerializer.descriptor.serialName}>") {
        element("col1", tSerializer.descriptor)
        element("col2", uSerializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): Row2<T, U> {
        var col1: T? = null
        var col2: U? = null
        decoder.decodeStructure(descriptor) {
            while (true) {
                when(decodeElementIndex(descriptor)){
                    0 -> col1 = decodeSerializableElement(descriptor, 0, tSerializer)
                    1 -> col2 = decodeSerializableElement(descriptor, 1, uSerializer)
                    CompositeDecoder.DECODE_DONE -> break
                }
            }
        }
        return Row2(col1!!, col2!!)
    }

    override fun serialize(encoder: Encoder, value: Row2<T, U>) {
        TODO()
    }
}

