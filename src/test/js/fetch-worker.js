const { Worker, isMainThread, workerData } = require('worker_threads');

if (isMainThread) {

    class FetchWorker {
        post(url, array, token, debug) {
            if (debug) {
                console.log(JSON.stringify(array))
            }
            const sharedBuffer = new SharedArrayBuffer(4);
            const sharedInt   = new Int32Array(sharedBuffer);
            const resultBuffer = new SharedArrayBuffer(4 * 1024 * 1024); // 4 MB
            const resultBytes  = new Uint8Array(resultBuffer);

            new Worker(__filename, { workerData: { url, array, token, sharedBuffer, resultBuffer } });

            Atomics.wait(sharedInt, 0, 0); // block until worker signals

            const length = Atomics.load(sharedInt, 0);
            const text   = Buffer.from(resultBytes.subarray(0, Math.abs(length))).toString();
            if (debug) {
                console.log("  -> " + text)
            }

            const parsed = JSON.parse(text);

            if (length < 0) throw new Error('Fetch failed: ' + parsed.__error);
            return parsed;
        }
    }

    module.exports = { FetchWorker };

} else {

    // ---- worker side ----
    const { url, array, token, sharedBuffer, resultBuffer } = workerData;
    const sharedInt  = new Int32Array(sharedBuffer);
    const resultBytes = new Uint8Array(resultBuffer);

    (async () => {
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/vnd.kcl.prov-template+json',
                    'Accept':        'application/vnd.kcl.prov-template+json'
                },
                body: JSON.stringify(array)
            });
            //console.log('Response status:', response.status);
            const text    = await response.text();
            const encoded = Buffer.from(text);
            resultBytes.set(encoded);
            Atomics.store(sharedInt, 0, encoded.length);
        } catch (e) {
            const encoded = Buffer.from(JSON.stringify({ __error: e.message }));
            resultBytes.set(encoded);
            Atomics.store(sharedInt, 0, -encoded.length);
        }
        Atomics.notify(sharedInt, 0, 1);
    })();
}
