package uk.gibby.dsl.functions

import uk.gibby.dsl.types.StringType
import uk.gibby.dsl.types.stringType

object Parse {

    object Email {

        fun domain(value: StringType) = stringType.createReference("parse::email::domain(${value.getReference()})")
        fun user(value: StringType) = stringType.createReference("parse::email::user(${value.getReference()})")
    }

    object Url {
        fun domain(value: StringType) = stringType.createReference("parse::url::domain(${value.getReference()})")
        fun fragment(value: StringType) = stringType.createReference("parse::url::fragment(${value.getReference()})")
        fun host(value: StringType) = stringType.createReference("parse::url::host(${value.getReference()})")
        fun port(value: StringType) = stringType.createReference("parse::url::port(${value.getReference()})")
        fun query(value: StringType) = stringType.createReference("parse::url::query(${value.getReference()})")
    }
}