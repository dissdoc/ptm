class AddThumbnailToAlbum < ActiveRecord::Migration
  def change
    add_column :albums, :thumbnail, :integer

  end
end
