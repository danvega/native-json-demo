# Native JSON in JDK 28

The JDK is getting its own JSON API. These small programs use it to parse a todo list, turn
JSON into Java records, and turn records back into JSON. No Jackson or any other library.

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

Then run any of the programs:

```sh
java --add-modules jdk.incubator.json NativeJsonDemo.java
```

```sh
java --add-modules jdk.incubator.json DeserializeTodos.java
```

```sh
java --add-modules jdk.incubator.json SerializeTodos.java
```

Java compiles and runs the file in one step, and finds `Todo.java` on its own. Incubator
modules are not loaded by default, so `--add-modules` is required. `--enable-preview` is not
needed. Each run starts with a warning that an incubator module is in use. That is expected.

## Where to look

Start with [NativeJsonDemo.java](NativeJsonDemo.java). It covers the whole API in three steps:

1. **Parse.** `Json.parse(String)` turns a JSON text block into a `JsonValue`.
2. **Read.** `get("owner")` reads a member. `asString()`, `asBoolean()`, and `asList()`
   convert values to Java types, so you can loop over the todos like any other list.
3. **Write.** `JsonString.of` and `JsonNumber.of` build new values. `JsonObject.of(map)` turns
   them into an object. `Json.toDisplayString(value, "  ")` prints it indented.

It prints:

```text
Dan's todos:
[x] Try the new JSON API
[ ] Record the video
{
  "owner": "Dan",
  "total": 2,
  "done": 1,
  "status": "keep going"
}
```

Then look at the other three files. They show how to work with your own types.

- [Todo.java](Todo.java) is a record that does the mapping by hand, in both directions.
  `Todo.from(JsonValue)` reads each field. A `due` date can be missing or `null`, so it uses
  `tryGet("due")` for a missing member and `tryValue()` for a `null` one. `toJson()` builds
  each field with `JsonNumber.of`, `JsonString.of`, `JsonBoolean.of`, `JsonArray.of`, and
  `JsonNull.of`.
- [DeserializeTodos.java](DeserializeTodos.java) parses a todo list and maps it to a
  `List<Todo>` with `Todo::from`. It prints the owner, then one line per todo:
  `Todo[id=1, title=Try the new JSON API, done=true, ...]`.
- [SerializeTodos.java](SerializeTodos.java) goes the other way. It turns a `List<Todo>` into
  a `JsonArray`, then prints it twice: compact with `toString()`, then indented. One title
  contains quotes, and the output escapes them for you.

The programs are compact source files (`void main()`, no class). That keeps the focus on the
JSON code.

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
- **Data binding.** Nothing maps JSON to your own classes or records. That is why `Todo.java`
  does it by hand. With a library like Jackson, each direction is usually a single call.
- **Streaming.** The whole document is parsed into memory. There is no token-by-token reader
  for very large files.
- **Options and variants.** There are no settings, no comments in JSON, and no lenient mode.

## Good to know

- This is an incubating API in an early-access JDK. Names and behavior can change in any
  build. Treat it as an experiment.
- `Json.parse` takes a `String` or `char[]`. Read a file into a string first.
- `asMap()` and `asList()` return unmodifiable collections.
- Invalid JSON throws `JsonParseException`. A missing member, the wrong type, or a bad index
  throws `JsonValueException`.
- In IntelliJ, set the project SDK to the JDK 28 build. Add `--add-modules jdk.incubator.json`
  as a javac option under Settings, Build, Execution, Deployment, Compiler, Java Compiler.
  Turn off "Use '--release' option for cross-compilation" in the same place. Otherwise the
  build fails with "module not found". Each run configuration needs the same flag under VM
  options (not Program arguments).
- [API docs for jdk.incubator.json](https://download.java.net/java/early_access/jdk28/docs/api/jdk.incubator.json/jdk/incubator/json/package-summary.html)
