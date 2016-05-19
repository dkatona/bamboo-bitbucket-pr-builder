package cz.katona.pr.builder.bamboo.model;

import static cz.katona.pr.builder.TestUtil.MAPPER;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

public class BranchTest {

    @Test
    public void testDeserialization() throws Exception {
        Branch branch = MAPPER.readValue(getClass().getResourceAsStream("/bamboo/model/branch.json"),
                Branch.class);
        assertThat(branch.getKey(), is("DP-FBB395"));
        assertThat(branch.getName(), is("My branch"));
        assertThat(branch.getShortName(), is("dk-test-int"));
        assertThat(branch.isEnabled(), is(true));
    }

    @Test
    public void testSerialization() throws Exception {
        Branch branch = new Branch("My branch","dk-abc", "DP-ABC35", false);
        String json = MAPPER.writeValueAsString(branch);
        assertThatJson(json)
                .node("name").matches(is("My branch"))
                .node("shortName").matches(is("dk-abc"))
                .node("key").matches(is("DP-ABC35"))
                .node("enabled").matches(is(false));
    }
}
