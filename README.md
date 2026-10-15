# Native JSON in JDK 28

The JDK is getting its own JSON API. These examples use it to parse JSON, map it to Java
records and back, and read JSON from a real API. No Jackson or any other library.

## Tested with

OpenJDK 28-ea+19 (early access) on macOS Apple Silicon. No Maven, Gradle, or dependencies.

## What you need

A JDK 28 early-access build that includes the incubator module `jdk.incubator.json`. This
demo was tested with the
[official 28-ea+19 build](https://download.java.net/java/early_access/jdk28/19/GPL/openjdk-28-ea+19_macos-aarch64_bin.tar.gz).

## Run it

First, switch to JDK 28. With SDKMAN, this only changes Java for the current terminal:

```sh
sdk use java 28.ea.19-open
```

Without SDKMAN, point `JAVA_HOME` at the JDK and put its `bin` folder first on your `PATH`.

Then run the getting-started example:

```sh
java --add-modules jdk.incubator.json GettingStarted.java
```

The other examples live in three packages. Run them from the project root:

```sh
java --add-modules jdk.incubator.json todo/DeserializeTodos.java
```

```sh
java --add-modules jdk.incubator.json todo/SerializeTodos.java
```

```sh
java --add-modules jdk.incubator.json posts/FetchPosts.java
```

```sh
java --add-modules jdk.incubator.json features/PatternMatching.java
```

```sh
java --add-modules jdk.incubator.json features/ErrorMessages.java
```

```sh
java --add-modules jdk.incubator.json features/Numbers.java
```

Java compiles and runs the file in one step, and finds the other files in its package on its
own. Incubator modules are not loaded by default, so `--add-modules` is required.
`--enable-preview` is not needed. Each run starts with a warning that an incubator module is
in use. That is expected.

## Where to look

### Getting started

[GettingStarted.java](GettingStarted.java) shows the whole API in a few lines. `Json.parse`
turns JSON text into a `JsonValue`. `get("name").asString()` reads a value. `JsonObject.of`
and `JsonString.of` build new JSON, and printing it writes compact JSON text:

```text
Dan is using Java 28
{"message":"Hello, Dan!"}
```

It's a compact source file (`void main()`, no class), so there's nothing else to read.

### Todos: JSON to records and back

The [todo](todo) package starts with [Todo.java](todo/Todo.java). It's a record that maps a
todo to and from JSON by hand:

- `Todo.from(JsonValue)` reads each field with `get("title")`, then converts it with
  `asString()`, `asInt()`, `asBoolean()`, or `asList()`. A `due` date can be missing or
  `null`, so it uses `tryGet("due")` for a missing member and `tryValue()` for a `null` one.
- `toJson()` builds each field with `JsonNumber.of`, `JsonString.of`, `JsonBoolean.of`,
  `JsonArray.of`, and `JsonNull.of`, then wraps them with `JsonObject.of`.

[DeserializeTodos.java](todo/DeserializeTodos.java) goes from JSON to Java. It parses a JSON
text block and maps the `todos` array to a `List<Todo>` with `Todo::from`. It prints the
owner, then one line per todo:

```text
Dan
Todo[id=1, title=Try the new JSON API, done=true, priority=2, tags=[java, jdk28], due=null]
Todo[id=2, title=Record the video, done=false, priority=1, tags=[youtube], due=2026-10-20]
Todo[id=3, title=Write the blog post, done=false, priority=3, tags=[], due=null]
```

[SerializeTodos.java](todo/SerializeTodos.java) goes from Java to JSON. It turns a
`List<Todo>` into a `JsonArray`, then prints it twice: compact with `toString()`, then
indented with `Json.toDisplayString(json, "  ")`. One title contains quotes, and the output
escapes them for you.

### Posts: JSON from a real API

The [posts](posts) package fetches JSON over HTTP, still with no libraries.
[FetchPosts.java](posts/FetchPosts.java) uses the JDK's `HttpClient` to get
`https://jsonplaceholder.typicode.com/posts`. It parses the response body and maps each item
to a [Post](posts/Post.java) record. It prints the count and the first five titles:

```text
Fetched 100 posts
1: sunt aut facere repellat provident occaecati excepturi optio reprehenderit
2: qui est esse
...
```

This one needs an internet connection.

### Features: what makes this API different

The [features](features) package has three short programs, one per feature.

[PatternMatching.java](features/PatternMatching.java) uses a `switch` over the six JSON types.
`JsonValue` is a sealed interface, so the compiler knows every case is covered and no
`default` branch is needed. A second `switch` handles a `tags` field that can be either a
string or an array:

```text
string: Java
number: 28
...
One tag: [java]
Many tags: [java, jdk28]
```

[ErrorMessages.java](features/ErrorMessages.java) breaks JSON on purpose. A missing member, a
wrong type, or a bad index throws `JsonValueException`. Comments, trailing commas, single
quotes, and duplicate keys throw `JsonParseException`, because the parser accepts standard
JSON only. Each message includes a path and a location:

```text
Wrong type: JsonString is not a JsonNumber. Path: "{title". Location: line 0, position 19.
Duplicate key: Duplicate member name: "id" was already parsed. Path: "{". Location: line 0, position 10.
```

`JsonParseException` also has `getErrorLine()` and `getErrorPosition()`. Line numbers start
at 0.

[Numbers.java](features/Numbers.java) shows that a `JsonNumber` keeps the exact number from
the JSON text. `asInt()` refuses a value that doesn't fit, and `new BigInteger(n.toString())`
reads a number too big for a `long`. Adding prices with `BigDecimal` stays exact, where
`double` does not:

```text
total with double: 0.30000000000000004
total with BigDecimal: 0.30
{"total":0.30}
```

## What JEP 540 adds

[JEP 540: Simple JSON API (Incubator)](https://openjdk.org/jeps/540) ships in JDK 28. It gives
the JDK a small, standard way to read and write JSON:

- **A strict parser.** `Json.parse` accepts standard JSON (RFC 8259) only. There is one
  parsing mode, and duplicate member names are an error.
- **A value model.** Parsing returns an immutable `JsonValue`. Each JSON type has its own
  interface: `JsonObject`, `JsonArray`, `JsonString`, `JsonNumber`, `JsonBoolean`, and
  `JsonNull`.
- **Navigation and conversion.** `get`, `tryGet`, and `tryValue` move through the tree.
  `asString`, `asInt`, `asLong`, `asDouble`, `asBoolean`, `asList`, and `asMap` turn values
  into Java types.
- **Generation.** The `of` methods build values. `toString()` writes compact JSON, and
  `Json.toDisplayString` writes it indented.

The goal is to handle everyday JSON with little ceremony, and without adding a dependency.

## What it's not trying to solve

The JEP keeps the API small on purpose. It leaves out:

- **Replacing Jackson or Gson.** The JEP says it is not trying to replace established JSON
  libraries. If an app outgrows this API, moving to a library is the expected path.
- **Data binding.** Nothing maps JSON to your own classes or records. See
  [Data binding: you map by hand](#data-binding-you-map-by-hand) below.
- **Streaming.** The whole document is parsed into memory. There is no token-by-token reader
  for very large files.
- **Options and variants.** There are no settings, no comments in JSON, and no lenient mode.

### Data binding: you map by hand

With Jackson, `mapper.readValue(json, Post.class)` creates a record for you. It matches JSON
names to field names, and annotations like `@JsonProperty` and `@JsonIgnore` handle the
exceptions. This API has none of that: no automatic mapping, no annotations, no reflection.

Instead, you write the mapping yourself, like `Todo.from()` and `Post.from()` in this repo.
Because each field is mapped in code, a JSON name that doesn't match your Java name needs no
annotation. You just read a different key:

```java
record Post(int authorId, String headline) {

    static Post from(JsonValue json) {
        return new Post(
                json.get("userId").asInt(),
                json.get("title").asString());
    }
}
```

The same goes for the other things annotations usually do. To skip a field, don't read it.
For a default value, use `tryGet` with `orElse`. Writing JSON works the same way in
`toJson()`: you pick each key name.

The trade-off is a few lines of code per field. In return, the mapping is plain Java you can
read and debug. If you have many types, or you want annotations, use a library like Jackson.
The JEP expects that.

## Good to know

- This is an incubating API in an early-access JDK. Names and behavior can change in any
  build. Treat it as an experiment.
- `Json.parse` takes a `String` or `char[]`. Read a file into a string first.
- `asMap()` and `asList()` return unmodifiable collections.
- `JsonValue` doesn't compare by value. Two values parsed from the same text are not `equals`.
  To compare, convert to Java values first, for example with `asString()` or `asInt()`.
- Invalid JSON throws `JsonParseException`. A missing member, the wrong type, or a bad index
  throws `JsonValueException`.
- In IntelliJ, set the project SDK to the JDK 28 build. Add `--add-modules jdk.incubator.json`
  as a javac option under Settings, Build, Execution, Deployment, Compiler, Java Compiler.
  Turn off "Use '--release' option for cross-compilation" in the same place. Otherwise the
  build fails with "module not found". Each run configuration needs the same flag under VM
  options (not Program arguments).
- [API docs for jdk.incubator.json](https://download.java.net/java/early_access/jdk28/docs/api/jdk.incubator.json/jdk/incubator/json/package-summary.html)
