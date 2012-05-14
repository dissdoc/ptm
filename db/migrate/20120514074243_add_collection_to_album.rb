class AddCollectionToAlbum < ActiveRecord::Migration
  def change
    add_column :albums, :collection, :boolean, :default => false
  end
end
