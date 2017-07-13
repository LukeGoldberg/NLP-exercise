package com.lonely.nlp.corpus;

public enum Nature {
    a,
    
    ad,
    
    ag,
    
    an,
    
    b,
    
    bg,
    
    c,
    
    d,
    
    dg,
    
    e,
    
    f,
    
    g,
    
    h,
    
    i,
    
    j,
    
    k,
    
    l,
    
    m,
    
    mg,
    
    n,
    
    ng,
    
    nr,

    ns,

    nt,
    
    nx,
    
    nz,
    
    o,
    
    p,
    
    q,
    
    r,
    
    rg,
    
    s,
    
    t,
    
    tg,
    
    u,
    
    v,
    
    vd,
    
    vg,
    
    vn,
    
    w,
    
    x,
    
    y,
    
    yg,
    
    z
        
    ;

    /**
     * 词性是否以该前缀开头<br>
     *     词性根据开头的几个字母可以判断大的类别
     * @param prefix 前缀
     * @return 是否以该前缀开头
     */
    public boolean startsWith(String prefix)
    {
        return toString().startsWith(prefix);
    }

    /**
     * 词性是否以该前缀开头<br>
     *     词性根据开头的几个字母可以判断大的类别
     * @param prefix 前缀
     * @return 是否以该前缀开头
     */
    public boolean startsWith(char prefix)
    {
        return toString().charAt(0) == prefix;
    }

    /**
     * 词性的首字母<br>
     *     词性根据开头的几个字母可以判断大的类别
     * @return
     */
    public char firstChar()
    {
        return toString().charAt(0);
    }
}
