package org.piangles.core.resources;

import java.util.ArrayList;
import java.util.List;

public final class ConsumerProperties
{
	private String groupId;
	private List<Topic> topics = null;
	
	public ConsumerProperties(String groupId)
	{
		this.groupId = groupId;
		this.topics = new ArrayList<>();
	}
	
	public String getGroupId()
	{
		return groupId;
	}
	
	public List<Topic> getTopics()
	{
		return topics;
	}

	public void setTopics(List<Topic> topics)
	{
		this.topics = topics;
	}

	public class Topic
	{
		public String topicName;
		public int partitionNo;
		public int offset;
		public boolean compacted;

		public Topic(String topicName, int partitionNo, boolean compacted)
		{
			this(topicName, partitionNo, compacted, 0);
		}

		public Topic(String topicName, int partitionNo, boolean compacted, int offset)
		{
			this.topicName = topicName;
			this.partitionNo = partitionNo;
			this.compacted = compacted;
			this.offset = offset;
		}

		@Override
		public String toString()
		{
			return "Topic [topicName=" + topicName + ", partitionNo=" + partitionNo + ", offset=" + offset + ", compacted=" + compacted + "]";
		}
	}

	@Override
	public String toString()
	{
		return "ConsumerProperties [groupId=" + groupId + ", topics=" + topics + "]";
	}
}
