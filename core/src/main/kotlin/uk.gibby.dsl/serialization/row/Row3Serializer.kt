package uk.gibby.dsl.serialization.row

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import uk.gibby.dsl.model.rows.Row3

class Row3Serializer<T : Any, U: Any, V: Any>(
    private val tSerializer: KSerializer<T>,
    private val uSerializer: KSerializer<U>,
    private val vSerializer: KSerializer<V>,
) : KSerializer<Row3<T, U, V>> {

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Row2<${tSerializer.descriptor.serialName}, ${uSerializer.descriptor.serialName}>") {
            element("col1", tSerializer.descriptor)
            element("col2", uSerializer.descriptor)
            element("col3", vSerializer.descriptor)
        }

    override fun deserialize(decoder: Decoder): Row3<T, U, V> {
        var col1: T? = null
        var col2: U? = null
        var col3: V? = null
        decoder.decodeStructure(descriptor) {
            while (true) {
                when(decodeElementIndex(descriptor)){
                    0 -> col1 = decodeSerializableElement(descriptor, 0, tSerializer)
                    1 -> col2 = decodeSerializableElement(descriptor, 1, uSerializer)
                    2 -> col3 = decodeSerializableElement(descriptor, 2, vSerializer)
                    CompositeDecoder.DECODE_DONE -> break
                }
            }
        }
        return Row3(col1!!, col2!!, col3!!)
    }

    override fun serialize(encoder: Encoder, value: Row3<T, U, V>) {
        TODO()
    }
}