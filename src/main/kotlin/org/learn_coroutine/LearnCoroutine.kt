package org.example.org.learn_coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.system.measureTimeMillis

fun learnCoroutine() {
    learnRunBlocking()
    cancelAndTimeout()
    getCoroutineResult()
    asyncStyleFunctions()
    errorHandling()
    stream()
    channel()
}

// Coroutine is a series of tasks, or a series of code in `launch` or `async` block.
// The advantage of coroutine is that it will automatically:
// 1. Run on another thread if we specified.
// 2. Switch to another thread if we want it to do by configuring inside the "coroutine code block".
// 3. "Detach" (which means other code can use the current thread) from thread if we "suspend" it, so it does not
//    "block" the "current thread".
// 4. Switch back to the current thread if works on other thread finished.
// So we can not care about thread "switching".
private fun learnRunBlocking() {
    // `runBlocking` creates a coroutine code world and also is a bridge between coroutine world and regular code world.
    // It creates a coroutine scope that allow coroutines running inside and itself is still a synchronous code running
    // in current thread, blocks current thread till all the code inside complete.
    runBlocking {
        // `launch` builds a coroutine and runs it.
        // This will not "block" thread so the following code (including `launch` and `println`) will run immediately
        // before the inside code finish.
        launch {
            println("delay 600")
            // `delay` will "suspend" the current coroutine but does not "block" the underlying thread, it allows other
            // coroutines to run and use the thread.
            //
            // From https://stackoverflow.com/a/62065829
            // We can see the `delay` function as telling the current thread:
            // "Ok, now I do not require you to run my code, you can run code from other people. But remember to run
            // `println("Am I running?")` for me after 600 milliseconds."
            delay(600L)
            println("Am I running?")
        }

        println("Hello 0")

        launch {
            println("delay 1000")
            delay(1000L)
            println("World!")
        }

        launch {
            println("delay 200")
            delay(200L)
            println("Am I running again?")
        }

        // The following code is not
        println("Hello")
    }

    // This block can not execute until the above block run finished.
    // Because `runBlock` will block the current thread till all its inside code complete.
    runBlocking {
        myDelay(100)
        println("I'm the second \"runBlocking\"")
    }

}

private suspend fun myDelay(length: Long) {
    // If we run sleep without `withContext` (or say, without creating a coroutine), we get a warning.
    withContext(Dispatchers.IO) {
        Thread.sleep(length)
    }
}

// `coroutineScope` runs like `runBlocking` but it does not block the thread.
// Alternatively it is a "suspend" function, should also run in coroutine scope (another `coroutineScope` or `runBlocking`).
private suspend fun doWorld() = coroutineScope {
    launch {
        delay(1000)
        println("world! (from doWorld)")
    }
    println("hello")
}

private fun cancelAndTimeout() = runBlocking {
    val job = launch {
        repeat(1000) { i ->
            println("job: I'm sleeping $i...")
            // `delay` is a suspend function, it will check cancellation.
            delay(500L)
        }
    }
    delay(1300L)
    println("main: going to cancel the job")
    job.cancel()
    job.join()
    // Or use `cancelAndJoin()`
    // job.cancelAndJoin()
    println("job canceled.")

    println("the following job can not cancel before finish:")
    println("BUT IT NOT WORKS")
    val startTime = System.currentTimeMillis()
    val job2 = launch {
        var nextPrintTime = startTime
        var i = 0
        while (i < 5) {
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("job2: I'm sleeping $i...")
                nextPrintTime += 500L
                i++
            }
        }
    }
    delay(1300L)
    println("main: job2 going to cancel")
    job2.cancelAndJoin()
    println("job2 quit")
}

private suspend fun doWork1(): Int {
    println("start work 1")
    delay(1000L)
    println("finish work 1")
    return 13
}

private suspend fun doWork2(): Int {
    println("start work 2")
    delay(1400L)
    println("finish work 2")
    return 29
}

// Use `async` and `await` keyword to get the return value of coroutine.
// NOTE: `await` will block current coroutine so the former two test consumes 2400ms and the latter two only 1400ms.
private fun getCoroutineResult() = runBlocking {
    val time1 = measureTimeMillis {
        val one = async { doWork1() }.await()
        val two = async { doWork2() }.await()
        println("getCoroutineResult: ${one + two}")
    }
    println("getCoroutineResult 1 finish in $time1 ms")

    val time2 = measureTimeMillis {
        val one = async { doWork1() }.await()
        val two = async { doWork2() }
        println("getCoroutineResult: ${one + two.await()}")
    }
    println("getCoroutineResult 2 finish in $time2 ms")

    val time3 = measureTimeMillis {
        val one = async { doWork1() }
        val two = async { doWork2() }.await()
        println("getCoroutineResult: ${one.await() + two}")
    }
    println("getCoroutineResult 3 finish in $time3 ms")

    val time4 = measureTimeMillis {
        val one = async { doWork1() }
        val two = async { doWork2() }
        println("getCoroutineResult: ${one.await() + two.await()}")
    }
    println("getCoroutineResult 4 finish in $time4 ms")

    val time5 = measureTimeMillis {
        val one = async(start = CoroutineStart.LAZY) { doWork1() }
        val two = async(start = CoroutineStart.LAZY) { doWork2() }
        // Without start, coroutine still start when calling `await`, but will run 2400 ms
        one.start()
        two.start()
        println("getCoroutineResult: ${one.await() + two.await()}")
    }
    println("getCoroutineResult 5 finish in $time5 ms")
}

// Wrap `doWork` in this async scope
// Add `Async` suffix to function name to remind this function only start the async actions.
@OptIn(DelicateCoroutinesApi::class)
private fun doWork1Async() = GlobalScope.async {
    doWork1()
}

@OptIn(DelicateCoroutinesApi::class)
private fun doWork2Async() = GlobalScope.async {
    doWork2()
}

// Run async functions, outside coroutine.
private fun asyncStyleFunctions() {
    val time = measureTimeMillis {
        // Here starts code outside coroutine.
        val one = doWork1Async()
        val two = doWork2Async()
        // Still need to get the result in coroutine.
        runBlocking {
            println("asyncStyleFunctions result is ${one.await() + two.await()}")
        }
    }
    println("asyncStyleFunctions finish in $time ms")
}

@OptIn(DelicateCoroutinesApi::class)
private fun errorHandling() = runBlocking {
    val handler = CoroutineExceptionHandler { _, exception -> println("CoroutineExceptionHandler got $exception") }

    val job = GlobalScope.launch(handler) {
        println("Throwing exception from launch")
        // This exception in thrown from coroutine and this coroutine is not in another coroutine.
        // So it is an uncaught exception.
        //
        // Can not catch by setting to LAZY and `start`/`join` in try/catch block
        // Only can catch by setting handler
        throw IndexOutOfBoundsException()
    }
    job.join()

    println("Join finished")
    // Or use handler to handle exception:
    // ... = GlobalScope.async(handler)
    val deferred = GlobalScope.async {
        println("Throwing exception from async")
        throw ArithmeticException()
    }

    try {
        deferred.await()
        println("Unreached")
    } catch (e: ArithmeticException) {
        println("Caught ArithmeticException")
    }
}

private fun stream() {
    // Make a stream synchronously.
    fun simple(): Sequence<Int> = sequence {
        for (i in 1..3) {
            Thread.sleep(100)
            // `yield` to make a stream
            yield(i)
        }
    }

    simple().forEach { v -> println(v) }

    // `Flow` is asynchronous `stream`.
    // Without `suspend` keyword.
    fun simple2(): Flow<Int> = flow {
        for (i in 1..3) {
            delay(100)
            // Use `emit` to yield a value.
            emit(i)
        }
    }

    runBlocking {
        launch {
            for (k in 1..3) {
                println("main: $k")
                delay(100)
            }
        }

        // Flow started only before it is going to be used.
        // Flow is lazy.
        simple2().collect { v -> println("Flow: $v") }
    }

    fun simple3(): Flow<Int> = flow {
        // Here should not call in:
        // 1. `withContext`.
        // 2. `launch`.
        // Otherwise, the value emitted will not be in the same coroutine with `collect`.
        for (i in 1..3) {
            delay(100)
            emit(i)
        }
    }

    runBlocking {
        // Here is safe to:
        // 1. Directly call.
        // 2. Call in `launch`.
        // 3. Call in `withContext(Dispatchers.Default)`.
        simple3().collect { v -> println("simple3: $v") }
    }
}

// Channel used to transfer a stream of values between coroutines.
@OptIn(ObsoleteCoroutinesApi::class)
private fun channel() {
    // Default when the `capacity` not set, it will wait for a `receive` before sending another value.
    // `capacity` works like a buffer that save values and makes the producer not blocking.
    // `capacity` works like channel buffer size in golang.
    val channel = Channel<Int>(10)
    runBlocking {
        launch {
            for (x in 1..5) {
                delay(200)
                val value = x * x
                println("channel: send $value")
                channel.send(value)
            }
        }
        launch {
            // We specified to `receive` how many times.
            // If receive times is more than send times, program blocks forever because the `receive` is never going to
            // finish.
            // If receive times is less than send times, program exits before send finished because the main thread
            // reached the end, just like a main goroutine finish will stop all other goroutines.
            repeat(5) {
                delay(400)
                // `receive` is a suspend function.
                println("channel: ${channel.receive()}")
            }
        }
    }


    // Convenience way to build a producer.
    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.produceSquares(): ReceiveChannel<Int> = produce {
        for (x in 1..5) {
            send(x * x)
        }
    }

    runBlocking {
        val squares = produceSquares()
        // Use `consumeEach` to take place of `for` loop.
        squares.consumeEach { println("consumeEach: $it") }
    }

    // Build a pipeline
    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.numbersFrom(start: Int) = produce(capacity = 4) {
        var x = start
        while (true) {
            delay(400)
            send(x++)
        }
    }

    // Make a filter to construct pipeline.
    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int) = produce {
        for (x in numbers) {
            if (x % prime != 0) {
                send(x)
            }
        }
    }

    runBlocking {
        var cur = numbersFrom(2)

        repeat(3) {
            val prime = cur.receive()
            println(prime)
            cur = filter(cur, prime)
        }
        coroutineContext.cancelChildren()
    }

    // Multiple coroutine send to a same channel
    suspend fun sendString(channel: SendChannel<Pair<String, String>>, s: Pair<String, String>, time: Long) {
        while (true) {
            delay(time)
            channel.send(s)
        }
    }

    runBlocking {
        val channel = Channel<Pair<String, String>>()
        launch { sendString(channel, Pair("hello", "foo"), 200L) }
        launch { sendString(channel, Pair("world", "BAR!"), 500L) }
        repeat(6) {
            val data = channel.receive()
            println("channel: receive ${data.second} form ${data.first}")
        }
        println("channel: done!")
        coroutineContext.cancelChildren()
    }

    data class Ball(var hits: Int)

    suspend fun player(name: String, table: Channel<Ball>) {
        for (ball in table) {
            ball.hits++
            println("$name $ball")
            delay(300)
            table.send(ball)
        }
    }

    runBlocking {
        // Two coroutines share the same channel.
        val table = Channel<Ball>()
        launch { player("ping", table) }
        launch { player("pong", table) }
        table.send(Ball(0))
        delay(2000)
        coroutineContext.cancelChildren()
    }

    // Ticker channels
    runBlocking {
        val tickerChannel = ticker(delayMillis = 100, initialDelayMillis = 0)
        var nextElement = withTimeoutOrNull(1) { tickerChannel.receive() }
        println("Initial element is available immediately: $nextElement")

        nextElement = withTimeoutOrNull(50) { tickerChannel.receive() }
        println("Next element is not ready in 50 ms: $nextElement")

        nextElement = withTimeoutOrNull(60) { tickerChannel.receive() }
        println("Next element is not ready in 60 ms: $nextElement")

        println("consumer pause for 150 ms")
        delay(150)
        nextElement = withTimeoutOrNull(1) { tickerChannel.receive() }
        println("Next element is available immediately: $nextElement")
        tickerChannel.cancel()
    }
}
