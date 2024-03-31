package com.fuzzy.cluster.component.memory;

import com.fuzzy.cluster.anotation.Info;
import com.fuzzy.cluster.component.memory.core.MemoryEngine;
import com.fuzzy.cluster.struct.Component;

/**
 * Created by kris on 17.10.16.
 */
@Info(uuid = "com.fuzzy.cluster.component.memory")
public class MemoryComponent extends Component {

    private MemoryEngine memoryEngine;

    public MemoryComponent() {
        this.memoryEngine = new MemoryEngine(this);
    }

    public MemoryEngine getMemoryEngine() {
        return memoryEngine;
    }
}
