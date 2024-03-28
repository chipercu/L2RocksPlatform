package com.fuzzy.main.cluster.component.memory;

import com.fuzzy.main.cluster.anotation.Info;
import com.fuzzy.main.cluster.component.memory.core.MemoryEngine;
import com.fuzzy.main.cluster.struct.Component;

/**
 * Created by kris on 17.10.16.
 */
@Info(uuid = "com.fuzzy.main.cluster.component.memory")
public class MemoryComponent extends Component {

    private MemoryEngine memoryEngine;

    public MemoryComponent() {
        this.memoryEngine = new MemoryEngine(this);
    }

    public MemoryEngine getMemoryEngine() {
        return memoryEngine;
    }
}
