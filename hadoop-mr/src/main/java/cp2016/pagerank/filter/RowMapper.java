package cp2016.pagerank.filter;

import cp2016.pagerank.common.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.alibaba.fastjson.JSON;

public class RowMapper extends Mapper<LongWritable, Text, TitleRankPair, Text> {
	@Override
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		Configuration config = context.getConfiguration();
		long numberOfTitles = config.getLong("numberOfTitles", 0);
		
		if (numberOfTitles == 0) {
			throw new RuntimeException("Number of titles is 0!");
		}
		
		String[] records = value.toString().split("\n");
		for (String r : records) {
			String[] kv = r.split("\t");
			String title = kv[0];
			String linksJSON = kv[1];
			
			List<String> links = JSON.parseArray(linksJSON, String.class);
			List<String> validLinks = new ArrayList<>();
			
			for (String link : links) {
				if (config.getBoolean("👉" + link, false)) {
					validLinks.add(link);
				}
			}
			
			context.write(new TitleRankPair(title, 1.0 / numberOfTitles),  new Text(JSON.toJSONString(validLinks)));
		}
	}
}