The 'node_usage.csv' file is used by the node repository to add a weight (i.e. their popularity) to nodes for proper sorting.
It is a condensed node usage file created from a recommendations file as used for the workflow coach
(e.g. downloaded from https://update.knime.com/community_recommendations_5.2.json).
The result file represents the node as a hash of the node's factory-id.
The file is created by the contained workflow ('condensed_node_usage.knwf') (NOTE: the workflow is outdated - doesn't calculate the hash based on the factory-id, yet)