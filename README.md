tetris-zio
============

This is a falling block game (Tetris clone) written in Scala using the ZIO library to manage state in a purely functional way.

The entire codebase is written in pure FP style with no variables and no mutable datastructures.  It illustrates case-class copying, (tail-) recursive state updates, side-effecting operations using ZIO Tasks and ZIO Schedule to trigger timer ticks and a just a couple ZIO `unsafeRun` sections for integration with the side-effecting Swing library.

## credits & attribution

The codebase is a hard-fork from https://github.com/eed3si9n/tetrix.scala.  It was heavily simplified to be a more compact example of using ZIO.  The module structure was reduced to a single module and the Android bits were discarded.  The existing codebase already followed a functional style.  The code was further cleaned up and tidied according to the author's (@aboisvert) style and taste and the effectful parts were moved into ZIO context, in particular moving all mutable state into a ZIO `Actor`.

The license remains MIT and copyright attribution can be obtained from the fork point at https://github.com/eed3si9n/tetrix.scala/commit/2bde9e2e1188300576980c729a05e63d2b7bd0b0.  Any code differences are copyright (c) 2020, Alex Boisvert.

How to build
------------

Use sbt. To run swing UI:

```
> sbt run
```

License
-------

MIT License.
