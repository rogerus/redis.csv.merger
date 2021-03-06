# Prerequisite - MergeSort

## Split redis.csv into smaller files and sort them individually
> split -l 200000 redis.csv  
> mkdir pars1 pars2 pars3 pars4  
> mv xa? xb? pars1/  
> mv xc? xd? pars2/  
> mv xe? xf? pars3/  
> mv xg? pars4/  
> vim sort.sh
```bash
#!/bin/bash
# sort.sh 
echo `date`
    for f in x* 
    do 
        cat $f | sort -k3 -t "," |  awk -F , '{print $3","$4}' > ../#$f.sort
    done 
echo `date`
```

> chmod +x sort.sh

> cp sort.sh pars1/  
> cp sort.sh pars2/  
> cp sort.sh pars3/  
> cp sort.sh pars4/  
> 
> rm -f sort.sh
> vim go.sh
```bash
#!/bin/bash
# go.sh
    for f in pars? 
    do 
        cd $f
        nohup ./sort.sh > nohup.out 2>&1 &
        cd ..
    done 
```


> chmod +x go.sh
> ./go.sh

## merge the sorted files
> find . -name '*.sort' -print0 | sort -k1 -t, --files0-from=- -S512m -o redis.sorted.csv >sort.out 2>sort.err &

## To verify if the output file is correctly sorted.
> sort -c redis.sorted.csv  


# Running Redis keys analysis

## Remove existing forest files (if necessary)

> rm -rf forrest/

## Run the programme
> cat redis.sorted.csv | java -cp ./{YOUR_ARTIFACT_JAR_PATH}:{DEPENDENCIES_JAR_PATH} com.tpadsz.utils.merger.KeyToTreeAnalyzer -Xmx{JAVA_HEAP_SIZE}

Note: {JAVA_HEAP_SIZE} depends on com.tpadsz.utils.merger.contants.Config.EVENT_QUEUE_SIZE of which default value is 100000 and may approximately consume at least 512MB memory. Reduce this value if the machine which this programme runs on don't have much memory.   


```java
package com.tpadsz.utils.merger.contants;

/**
 * Created by roger.wang on 2016/1/27.
 */
public class Config {
    public static final int EVENT_QUEUE_SIZE=100000;
}
```

