class RemoveGeoFromRecommendGeo < ActiveRecord::Migration
  def up
    remove_column :recommend_geos, :geo_id
      end

  def down
    add_column :recommend_geos, :geo_id, :integer
  end
end
