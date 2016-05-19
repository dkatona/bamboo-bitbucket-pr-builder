package cz.katona.pr.builder.bamboo.model;

import static cz.katona.pr.builder.TestUtil.MAPPER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

public class JobQueuedTest {

    @Test
    public void testDeserialization() throws Exception {
        JobQueued jobQueued = MAPPER.readValue(getClass().getResourceAsStream("/bamboo/model/jobQueued.json"),
                JobQueued.class);
        assertThat(jobQueued.getBuildNumber(), is(3));
        assertThat(jobQueued.getPlanKey(), is("DP-FBB371"));
        assertThat(jobQueued.getBuildResultKey(), is("DP-FBB371-3"));
        assertThat(jobQueued.getTriggerReason(), is("Manual build"));
    }

    @Test
    public void testJobBuildLink() throws Exception {
        JobQueued jobQueued = new JobQueued("DP-ABC", 2, "DP-ABC-2", "Manual");
        assertThat(jobQueued.getJobBuildLink("https://my.bamboo.com"), is("https://my.bamboo.com/DP-ABC-2"));
    }
}
