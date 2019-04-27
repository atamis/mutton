# atamis/mutton

Simple interpreter for lambda calculus.

Use `atamis.mutton/simplify` to simplify a lambda calclus term according to the
following rules

```
    x -> x
    (l x y) -> function that binds x in y body
    (x y) -> applies function x to body y
```

It uses `clojure.spec.alpha` to verify the syntax of the term, and
`atamis.mutton/unconform` to convert a conformed term to a normal term.


```
(time (try (simplify '((l x (x x)) (l x (x x))))
                          (catch Error e (println "Got error" e))
                          ))

...
                          
"Elapsed time: 18.542197 msecs"
```



## License

Copyright Andrew Amis 2019.

Eclipse License with Classpath Exception, or whatever.

