package uk.gibby.dsl.types

import uk.gibby.dsl.core.Table
import uk.gibby.dsl.model.Linked


interface RelationType<a, A: RecordType<a>, b, c, C: RecordType<c>>: RecordType<b>{
    val `in`: RecordLink<a, A>
    val `out`: RecordLink<c, C>
}

fun <a, A: RecordType<a>, b, B: RelationType<a, A, b, c, C>, c, C: RecordType<c>>A.`o-→`(to: Table<b, B>): ListType<Linked<b>, RecordLink<b, B>>{
    return if(getReference() == "_") ListType(linked(to), "->" + to.name)
        else ListType(linked(to), "${getReference()}->${to.name}")
}

fun <a, A: RecordType<a>, b, B: RelationType<a, A, b, c, C>, c, C: RecordType<c>>A.`o-→`(to: Table<b, B>, where: B.() -> BooleanType): ListType<Linked<b>, RecordLink<b, B>> {
    val filter = to.recordType.where().getReference()
    return if(getReference() == "_") ListType(linked(to), "->(${to.name} WHERE $filter)")
    else ListType(linked(to), "${getReference()}->(${to.name} WHERE $filter)")
}

@JvmName("rightArrow")
fun <a, A: RecordType<a>, b, B: RelationType<a, A, b, c, C>, c, C: RecordType<c>>ListType<Linked<a>, RecordLink<a, A>>.`o-→`(to: Table<b, B>): ListType<Linked<b>, RecordLink<b, B>>{
    return if(getReference() == "_") ListType(with(inner.inner){ linked(to) } , "->" + to.name)
    else ListType(with(inner.inner){ linked(to) }, "${getReference()}->${to.name}")
}

@JvmName("rightArrow")
fun <a, A: RecordType<a>, b, B: RelationType<a, A, b, c, C>, c, C: RecordType<c>>ListType<Linked<a>, RecordLink<a, A>>.`o-→`(to: Table<b, B>, where: B.() -> BooleanType): ListType<Linked<b>, RecordLink<b, B>>{
    val filter = to.recordType.where().getReference()
    return if(getReference() == "_") ListType(with(inner.inner){ linked(to) } , "->(${to.name} WHERE $filter)")
    else ListType(with(inner.inner){ linked(to) }, "${getReference()}->(${to.name} WHERE $filter)")
}
fun <a, A: RecordType<a>, b, B: RelationType<a, A, b, c, C>, c, C: RecordType<c>>ListType<Linked<b>, RecordLink<b, B>>.`o-→`(to: Table<c, C>): ListType<Linked<c>, RecordLink<c, C>> {
    return if(getReference() == "_") ListType(inner.inner.out, "->${to.name}")
    else ListType(inner.inner.out, "${getReference()}->${to.name}")
}

fun <a, A: RecordType<a>, b, B: RelationType<a, A, b, c, C>, c, C: RecordType<c>>ListType<Linked<b>, RecordLink<b, B>>.`o-→`(to: Table<c, C>, where: C.() -> BooleanType): ListType<Linked<c>, RecordLink<c, C>> {
    val filter = to.recordType.where().getReference()
    return if(getReference() == "_") ListType(inner.inner.out, "->(${to.name} WHERE $filter)")
    else ListType(inner.inner.out, "${getReference()}->(${to.name} WHERE $filter)")
}

fun <a, A: RecordType<a>, b, B: RelationType<c, C, b, a, A>, c, C: RecordType<c>>A.`←-o`(to: Table<b, B>): ListType<Linked<b>, RecordLink<b, B>>{
    return if(getReference() == "_") ListType(linked(to), "<-" + to.name)
    else ListType(linked(to), "${getReference()}<-${to.name}")
}

fun <a, A: RecordType<a>, b, B: RelationType<c, C, b, a, A>, c, C: RecordType<c>>A.`←-o`(to: Table<b, B>, where: B.() -> BooleanType): ListType<Linked<b>, RecordLink<b, B>>{
    val filter = to.recordType.where().getReference()
    return if(getReference() == "_") ListType(linked(to), "<-(${to.name} WHERE $filter)")
    else ListType(linked(to), "${getReference()}<-(${to.name} WHERE $filter)")
}

@JvmName("leftArrow")
fun <a, A: RecordType<a>, b, B: RelationType<c, C, b, a, A>, c, C: RecordType<c>>ListType<Linked<b>, RecordLink<b, B>>.`←-o`(to: Table<c, C>): ListType<Linked<c>, RecordLink<c, C>> {
    return if(getReference() == "_") ListType(inner.inner.`in`, "<-${to.name}")
    else ListType(inner.inner.`in`, "${getReference()}<-${to.name}")
}

@JvmName("leftArrow")
fun <a, A: RecordType<a>, b, B: RelationType<c, C, b, a, A>, c, C: RecordType<c>>ListType<Linked<b>, RecordLink<b, B>>.`←-o`(to: Table<c, C>, where: C.() -> BooleanType): ListType<Linked<c>, RecordLink<c, C>> {
    val filter = to.recordType.where().getReference()
    return if(getReference() == "_") ListType(inner.inner.`in`, "<-(${to.name} WHERE $filter)")
    else ListType(inner.inner.`in`, "${getReference()}<-(${to.name} WHERE $filter)")
}

fun <a, A: RecordType<a>, b, B: RelationType<c, C, b, a, A>, c, C: RecordType<c>>ListType<Linked<a>, RecordLink<a, A>>.`←-o`(to: Table<b, B>): ListType<Linked<b>, RecordLink<b, B>>{
    return if(getReference() == "_") ListType(with(inner.inner){ linked(to) } , "<-" + to.name)
    else ListType(with(inner.inner){ linked(to) }, "${getReference()}<-${to.name}")
}


fun <a, A: RecordType<a>, b, B: RelationType<c, C, b, a, A>, c, C: RecordType<c>>ListType<Linked<a>, RecordLink<a, A>>.`←-o`(to: Table<b, B>, where: B.() -> BooleanType): ListType<Linked<b>, RecordLink<b, B>>{
    val filter = to.recordType.where().getReference()
    return if(getReference() == "_") ListType(with(inner.inner){ linked(to) } , "<-(${to.name} WHERE $filter)")
    else ListType(with(inner.inner){ linked(to) }, "${getReference()}<-(${to.name} WHERE $filter)")
}

val <T, U: RecordType<T>>RecordLink<T, U>.STAR: U
    get() = inner.createReference(getReference() + ".*") as U
val <T, U: RecordType<T>>ListType<Linked<T>, RecordLink<T, U>>.STAR: ListType<T, U>
    get() = list(inner.inner).createReference(getReference() + ".*")

/*
fun <T, U: RecordType<T>, r, R: Reference<r>>ListType<Linked<T>, RecordLink<T, U>>.linked(projection:  U.() -> R): ListType<r, R> =
    inner.inner.projection().let { list(it).createReference(it.getReference()) }

 */
