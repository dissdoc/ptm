class AddPhotoToRecommendGeo < ActiveRecord::Migration
  def change
    add_column :recommend_geos, :photo_id, :integer

  end
end
