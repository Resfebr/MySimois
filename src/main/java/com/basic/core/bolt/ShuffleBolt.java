package com.basic.core.bolt;

import com.basic.core.util.FileWriter;
import com.basic.core.util.GeoHash;
import com.basic.core.util.Stopwatch;
import com.google.common.collect.ImmutableList;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.List;
import java.util.Map;

public class
ShuffleBolt extends BaseRichBolt {
    private static final List<String> SCHEMA = ImmutableList.of("relation", "timestamp", "key", "value");
    private OutputCollector _collector;
    private FileWriter _output;
    private long _r;
    private long _s;
    private long _lastTime;
    private Stopwatch _stopwatch;
    private String _rStream;
    private String _sStream;

    public ShuffleBolt(String datasize) {
        _rStream = "didiOrder" + datasize;
        _sStream = "didiGps" + datasize;
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this._collector = outputCollector;
        _output = new FileWriter("/tmp/", "zsj_shuffle" + topologyContext.getThisTaskId(), "txt");
        _r = 0;
        _s = 0;
        _lastTime = 0;
        _stopwatch = Stopwatch.createStarted();

    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(SCHEMA));
    }

    @Override
    public void execute(Tuple tuple) {
        String topic = tuple.getStringByField("topic");
        String value = tuple.getStringByField("value");
        String rel;
        Long ts = System.currentTimeMillis();
        String[] cols = value.split(",");
        //output(value);
        String key = "";
        if (topic.equals(_rStream)) {
            rel = "R";
            //cols[1] is order id
            key = GeoHash.encode(Double.parseDouble(cols[4]), Double.parseDouble(cols[3]), 7).toHashString();
            _r++;
        } else if (topic.equals(_sStream)) {
            rel = "S";
            //cols[0] is order id
            key = GeoHash.encode(Double.parseDouble(cols[4]), Double.parseDouble(cols[3]), 7).toHashString();
            _s++;
        } else {
            rel = "false";
        }
        _collector.emit(new Values(rel, ts, key, value));
    }

    private void output(String msg) {
        if (_output != null) {
            _output.write(msg);
            //_output.writeImmediately(msg);

        }

    }

    @Override
    public void cleanup() {
        if (_output != null) {
            _output.endOfFile();
        }
    }
}
