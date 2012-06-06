class AddCommentToPhoto < ActiveRecord::Migration
  def change
    add_column :photos, :comment, :string

  end
end
