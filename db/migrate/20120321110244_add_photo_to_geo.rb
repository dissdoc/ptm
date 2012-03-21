class AddPhotoToGeo < ActiveRecord::Migration
  def change
    add_column :geos, :photo_id, :integer

  end
end
