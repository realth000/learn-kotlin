package org.example.org.learn_function

/// Base abstract class
interface Element {
    fun render(builder: StringBuilder, indent: String)
}

class TextElement(val text: String) : Element {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$text\n")
    }
}

/// Annotation to limit structure.
/// For example, only allow Head node in Html scope.
@DslMarker
annotation class HtmlTagMarker

/// Use this marker to let compiler check whether a tag is properly use in allowed scope.
///
/// For example:
/// abstract class Foo(name: String) : Tag(name) { fun foo1(init: Foo1.() -> Unit) }
/// abstract class Bar(name: String) : Tag(name){ fun bar1(init: Bar1.() -> Unit) }
/// class Foo1 : Foo("foo1")
/// class Bar1 : Bar("bar1)
///
/// Will limit only nearest functions can be used as receiver.
/// That is, Foo1 can be used only in Foo and Bar1 can be used only in Bar.
@HtmlTagMarker
abstract class TagWithText(val name: String) : Element {
    /// Children nodes
    val children = arrayListOf<Element>()

    /// HashMap to record all attributes on current node.
    val attributes = hashMapOf<String, String>()

    /// Run lambda `init` and add `tag` to `children`
    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    /// Convert data in current node and children nodes to xml format
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent<$name${renderAttributes()}>\n")
        for (c in children) {
            c.render(builder, "$indent  ")
        }
        builder.append("$indent</$name>\n")
    }

    /// Format attr and its value
    private fun renderAttributes(): String {
        val builder = StringBuilder()
        for ((attr, value) in attributes) {
            builder.append(" $attr=\"$value\"")
        }
        return builder.toString()
    }

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }

    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }

    fun addAttr(key: String, value: String) {
        attributes[key] = value
    }

    fun getAttr(): HashMap<String, String> {
        return attributes
    }
}

/// head and body can only be used in "html" scope.
class HTML : TagWithText("html") {
    /// "head" scope, build a "Head" class.
    fun head(init: Head.() -> Unit) = initTag(Head(), init)

    /// "body" scope, build a "Body" class.
    fun body(init: Body.() -> Unit) = initTag(Body(), init)
}

/// "title" scope can only be used in "head" scope.
class Head : TagWithText("head") {
    /// "title" scope, build a "Title" class.
    fun title(init: Title.() -> Unit) = initTag(Title(), init)
}

class Title : TagWithText("title")

/// Tag types in "body" scope.
/// All these functions are used to make the structure of DSL.
/// Nested together to get the full output.
///
/// "b", "p", "h1", "a" can only be used in "name" (which is actually "body") scope.
abstract class BodyTag(name: String) : TagWithText(name) {
    fun b(init: B.() -> Unit) = initTag(B(), init)
    fun p(init: P.() -> Unit) = initTag(P(), init)
    fun h1(init: H1.() -> Unit) = initTag(H1(), init)

    /// "a" scope can have "href" attribute.
    fun a(href: String, init: A.() -> Unit) {
        val a = initTag(A(), init)
        a.href = href
    }
}

class Body : BodyTag("body")
class B : BodyTag("b")
class P : BodyTag("p")
class H1 : BodyTag("h1")

/// "a" scope is a little bit special, it can have a pairs of key-value attributes.
/// Now only support "href" attribute.
class A : BodyTag("a") {
    var href: String
        get() = attributes["href"]!!
        set(value) {
            attributes["href"] = value
        }
}

/// "html" scope, only receive one lambda parameter and call it.
fun html(init: HTML.() -> Unit): HTML {
    val html = HTML()
    html.init()
    return html
}

/// Parse and build to xml format (html) output.
fun buildHTML(vararg args: String) = html {
    head {
        title { +"XML encoding with Kotlin" }
    }
    body {
        h1 { +"XML Encoding with Kotlin HTML Builder" }
        p { +"this format can be used as an alternative markup to XML" }

        a(href = "https://kotlinlang.org") { +"Kotlin" }

        p {
            +"This is some"
            b { +"mixed BOLD text" }
            +"text. For more see the"
            a(href = "https://kotlinlang.org") { +"kotlin" }
            +"project"
        }

        p { +"also some text" }

        p {
            for (arg in args)
                +arg
        }
    }
}

